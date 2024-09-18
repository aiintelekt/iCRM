package org.fio.crm.ajax;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.fio.admin.portal.constant.AdminPortalConstant.DateTimeTypeConstant;
import org.fio.crm.constants.CrmConstants;
import org.fio.crm.constants.CrmConstants.ValidationAuditType;
import org.fio.crm.constants.ResponseCodes;
import org.fio.crm.party.PartyContactHelper;
import org.fio.crm.party.PartyHelper;
import org.fio.crm.util.DataUtil;
import org.fio.crm.util.LoginFilterUtil;
import org.fio.crm.util.ResponseUtils;
import org.fio.crm.util.UtilCommon;
import org.fio.crm.util.ValidatorUtil;
import org.fio.crm.util.VirtualTeamUtil;
import org.fio.crm.writer.WriterUtil;
import org.groupfio.common.portal.util.UtilCampaign;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.CommonWorkers;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.google.gson.Gson;

import au.com.bytecode.opencsv.CSVReader;
import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public final class AjaxEvents {

	private AjaxEvents() { }

	private static final String MODULE = AjaxEvents.class.getName();
	public static final String SESSION_KEY = "__LEAD_MODULE__";

	public static String doJSONResponse(HttpServletResponse response, JSONObject jsonObject) {
		return doJSONResponse(response, jsonObject.toString());
	}

	public static String doJSONResponse(HttpServletResponse response, Collection<?> collection) {
		return doJSONResponse(response, JSONArray.fromObject(collection).toString());
	}

	public static String doJSONResponse(HttpServletResponse response, Map map) {
		return doJSONResponse(response, JSONObject.fromObject(map));
	}
	public static AjaxEvents getInstance(HttpServletRequest request) {
		HttpSession session = request.getSession();
		AjaxEvents status = (AjaxEvents) session.getAttribute(SESSION_KEY);
		if (status == null) {
			status = new AjaxEvents();
			session.setAttribute(SESSION_KEY, status);
		}
		return status;
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

	public static GenericValue getUserLogin(HttpServletRequest request) {
		HttpSession session = request.getSession();
		return (GenericValue) session.getAttribute("userLogin");
	}

	/** Gets a list of states (provinces) that are associated with a given countryGeoId. */
	public static String getStateDataJSON(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String countryGeoId = request.getParameter("countryGeoId");

		try {
			Collection<GenericValue> states = CommonWorkers.getAssociatedStateList(delegator, countryGeoId);
			return doJSONResponse(response, states);
		} catch (Exception e) {
			return doJSONResponse(response, FastList.newInstance());
		}
	}


	/**
	 * Loads the given email template and perform substitutions on the subject and body according to the email context (recipient, related order / shipment / ...).
	 * @param request a <code>HttpServletRequest</code> value
	 * @param response a <code>HttpServletResponse</code> value
	 * @return a <code>String</code> value
	 * @exception GenericEntityException if an error occurs
	 */
	public static String getMergedFormForEmailJSON(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		String mergeFormId = request.getParameter("mergeFormId");
		// should tags that are not substituted left verbatim in the result, else they are blanked
		boolean leaveTags = !("false".equalsIgnoreCase(request.getParameter("reportType")) || "N".equalsIgnoreCase(request.getParameter("reportType")));
		// should tags that are not substituted highlighted in the result
		boolean highlightTags = !("false".equalsIgnoreCase(request.getParameter("highlightTags")) || "N".equalsIgnoreCase(request.getParameter("highlightTags")));

		Map < String, String > returnMap = new HashMap < String, String > ();
		GenericValue mergeForm = delegator.findOne("MergeForm", UtilMisc.toMap("mergeFormId", mergeFormId), false);
		if (UtilValidate.isNotEmpty(mergeForm)) {

			String partyId = null;
			String toEmail = request.getParameter("toEmail");
			if (UtilValidate.isNotEmpty(toEmail)) {
				toEmail = toEmail.trim();

				// Find the first party which matches one of the emails for the merge context
				List < String > partyIds = PartyContactHelper.getPartyIdsMatchingEmailsInString(delegator, toEmail, ",");
				if (UtilValidate.isNotEmpty(partyIds)) {
					partyId = partyIds.get(0);
				}
			}

			String orderId = request.getParameter("orderId");
			String shipGroupSeqId = request.getParameter("shipGroupSeqId");
			String shipmentId = request.getParameter("shipmentId");
			Map < String, String > output = PartyHelper.mergePartyWithForm(delegator, mergeFormId, partyId, orderId, shipGroupSeqId, shipmentId, locale, leaveTags, timeZone, highlightTags);

			returnMap.put("mergeFormText", output.get("mergeFormText"));
			returnMap.put("subject", output.get("subject"));
		}
		return AjaxEvents.doJSONResponse(response, returnMap);
	}

	/**
	 * Loads the given email template and perform substitutions on the subject and body according to the email context (recipient, related order / shipment / ...).
	 * @param request a <code>HttpServletRequest</code> value
	 * @param response a <code>HttpServletResponse</code> value
	 * @return a <code>String</code> value
	 * @exception GenericEntityException if an error occurs
	 */
	public static String getTemplateMasterForEmailJSON(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String templateId = request.getParameter("templateId");
		Map < String, String > returnMap = new HashMap < String, String > ();
		if (UtilValidate.isNotEmpty(templateId)) {

			GenericValue templateMaster = delegator.findOne("TemplateMaster", UtilMisc.toMap("templateId", templateId), false);
			if (UtilValidate.isNotEmpty(templateMaster)) {

				String textContent = templateMaster.getString("textContent");
				String subject = templateMaster.getString("subject");
				returnMap.put("textContent", textContent);
				returnMap.put("subject", subject);
			}
		}
		return AjaxEvents.doJSONResponse(response, returnMap);
	}
	@SuppressWarnings("unchecked")
	public static String batchUpdatePartyMetricIndicator(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);

		String partyId[] = request.getParameterValues("partyId");
		String groupingCode[] = request.getParameterValues("groupingCode");
		String propertyName[] = request.getParameterValues("propertyName");
		String sequenceNumber[] = request.getParameterValues("sequenceNumber");

		Map<String, Object> resp = new HashMap<String, Object>();

		try {

			if (UtilValidate.isNotEmpty(partyId)) {

				for (int i = 0; i < partyId.length; i++) {

					GenericValue partyMetric = EntityUtil.getFirst( delegator.findByAnd("PartyMetricIndicator", UtilMisc.toMap("partyId", partyId[i], "groupingCode", groupingCode[i], "propertyName", propertyName[i]), null, false) );

					if (UtilValidate.isNotEmpty(partyMetric)) {
						partyMetric.put("sequenceNumber", UtilValidate.isNotEmpty(sequenceNumber[i]) ? Long.parseLong(sequenceNumber[i]) : new Long(0));
						partyMetric.store();
					}

				}

			}

			resp.put("code", 200);

			resp.put("message", "Successfully updated!!");

		} catch (Exception e) {

			resp.put("code", 500);
			resp.put("message", "Error: "+e.getMessage());

			Debug.logError(e.getMessage(), MODULE);
		}

		return doJSONResponse(response, resp);
	}

	public static String getTeamMembers(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String draw = request.getParameter("draw");
		String start = request.getParameter("start");
		String length = request.getParameter("length");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String rmPartyId = request.getParameter("rmPartyId");
		String partyId = request.getParameter("partyId");
		String roleTypeId = request.getParameter("roleTypeId");
		String skipAssigned = request.getParameter("skipAssigned");
		String marketingCampaignId = request.getParameter("marketingCampaignId");
		String isActiveTeamMember = request.getParameter("isActiveTeamMember");
		String skipLeaders = request.getParameter("skipLeaders");
		Map < String, Object > returnMap = FastMap.newInstance();
		List < Object > partyRoleList = FastList.newInstance();
		try {
			List < EntityCondition > conditions = new ArrayList < EntityCondition > ();

			// construct role conditions
			if(UtilValidate.isNotEmpty(roleTypeId)) {
				conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));
			} else if("Y".equals(org.fio.homeapps.util.DataUtil.isPhoneCampaignEnabled(delegator))) {
				conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, UtilMisc.toList("ACCOUNT_MANAGER","CUST_SERVICE_REP")));
			} else {
				conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ACCOUNT_MANAGER"));
			}
			
			if(UtilValidate.isNotEmpty(skipAssigned) && "Y".equals(skipAssigned) && UtilValidate.isNotEmpty(marketingCampaignId)) {
				List<String> assignedCsrs = EntityUtil.getFieldListFromEntityList(EntityQuery.use(delegator).from("CampaignCsrAssoc").where("campaignId", marketingCampaignId).queryList(),"csrPartyId",true);
				if(UtilValidate.isNotEmpty(assignedCsrs))
					conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.NOT_IN, assignedCsrs));
			}
			
			if(UtilValidate.isNotEmpty(isActiveTeamMember) && "Y".equals(isActiveTeamMember)) {
				List<String> activeTeamMembers = null;
				if(UtilValidate.isNotEmpty(skipLeaders) && "Y".equals(skipLeaders)) {
					activeTeamMembers = UtilCampaign.getCsrMembersExcludeLeadersList(delegator, null, true);
				}else {
					activeTeamMembers = UtilCampaign.getCsrList(delegator, null, true);
				}
				if(UtilValidate.isNotEmpty(activeTeamMembers))
					conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, activeTeamMembers));
			}

			// construct search conditions
			if (UtilValidate.isNotEmpty(lastName)) {
				conditions.add(EntityCondition.makeCondition("lastName", EntityOperator.LIKE, EntityFunction.UPPER("%" + lastName + "%")));
			}
			if (UtilValidate.isNotEmpty(firstName)) {
				conditions.add(EntityCondition.makeCondition("firstName", EntityOperator.LIKE, EntityFunction.UPPER("%" + firstName + "%")));
			}
			if (UtilValidate.isNotEmpty(rmPartyId)) {
				conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, rmPartyId));
			}
			if (UtilValidate.isNotEmpty(partyId)) {
				conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			}
			// remove disabled parties
			conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
					EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, CrmConstants.PartyStatus.PARTY_DISABLED),
					EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)));

			Debug.logInfo("=====================conditions======================"+conditions, MODULE);
			
			EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			EntityFindOptions efo = new EntityFindOptions();
			efo.setDistinct(true);
			int startInx = UtilValidate.isNotEmpty(start) ? Integer.parseInt(start) : 0;
			int endInx = UtilValidate.isNotEmpty(length) ? Integer.parseInt(length) : 0;
			efo.setOffset(startInx);
			efo.setLimit(endInx);
			
			long count = 0;
			EntityFindOptions  efoNum= new EntityFindOptions();
			efoNum.setDistinct(true);
			efoNum.getDistinct();
			efoNum.setFetchSize(1000);
			
			count = delegator.findCountByCondition("PartyToSummaryByRole", mainConditons, null, UtilMisc.toSet("partyId"), efoNum);
			
			/*List < GenericValue > partyToSummartyByRoleCount = delegator.findList("PartyToSummaryByRole", mainConditons, null, null, null, false);
			int count = partyToSummartyByRoleCount.size();*/
			
			long recordsFiltered = count;
			long recordsTotal = count;
			List<String>possibleRoleTypeIds= new ArrayList<>();
			possibleRoleTypeIds.add("CUST_SERVICE_REP");
			possibleRoleTypeIds.add("ACCOUNT_MANAGER");

			List<GenericValue> roleTypes = delegator.findList("RoleType", null, UtilMisc.toSet("roleTypeId","description"), null, null, true);
			Map<String, Object> roleNames = org.fio.homeapps.util.DataUtil.getMapFromGeneric(roleTypes, "roleTypeId", "description", false);

			List < GenericValue > partyToSummartyByRole = delegator.findList("PartyToSummaryByRole", mainConditons, UtilMisc.toSet("partyId","firstName","lastName"), null, efo, false);
			if (partyToSummartyByRole != null && partyToSummartyByRole.size() > 0) {
				int id = 1;
				for (GenericValue roles: partyToSummartyByRole) {
					Map < String, Object > partyToSummartyByRoleMap = FastMap.newInstance();
					id = id + 1;
					partyToSummartyByRoleMap.put("id", id + "");
					String paryIdValue=roles.getString("partyId");
					List<String> matchingRoleTypeIds=org.groupfio.common.portal.util.DataUtil.getPartyRoles(paryIdValue, possibleRoleTypeIds, delegator);
					String concatenatedDescriptions = "";
					for (String matchingRoleTypeId : matchingRoleTypeIds) {
					    String description = (String) roleNames.get(matchingRoleTypeId);
					    if (description != null) {
					        if (!concatenatedDescriptions.isEmpty()) {
					            concatenatedDescriptions += ", ";
					        }
					        concatenatedDescriptions += description;
					    }
					}
					partyToSummartyByRoleMap.put("roleTypeIdName", concatenatedDescriptions);

					partyToSummartyByRoleMap.put("name", roles.getString("firstName") + " " + roles.getString("lastName"));
					partyToSummartyByRoleMap.put("partyId", roles.getString("partyId"));
					partyRoleList.add(partyToSummartyByRoleMap);
				}
				returnMap.put("data", partyRoleList);
				returnMap.put("draw", draw);
				returnMap.put("recordsTotal", recordsTotal);
				returnMap.put("recordsFiltered", recordsFiltered);
			} else {
				returnMap.put("data", partyRoleList);
				returnMap.put("draw", draw);
				returnMap.put("recordsTotal", 0);
				returnMap.put("recordsFiltered", 0);
				return AjaxEvents.doJSONResponse(response, returnMap);
			}
		} catch (Exception e) {
			Debug.logError("Exception in Get Team Member" + e.getMessage(), MODULE);
		}
		return AjaxEvents.doJSONResponse(response, returnMap);
	}

	/*
	 * Get Lead Details 
	 */
	public static String getLeadDetails(HttpServletRequest request, HttpServletResponse response) {
		
		HttpSession session = request.getSession();
	
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
	
		String draw = request.getParameter("draw");
		String start = request.getParameter("start");
		String length = request.getParameter("length");
		String partyId = request.getParameter("partyId");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String companyName = request.getParameter("companyName");
		String emailAddress = request.getParameter("emailAddress");
		String contactNumber = request.getParameter("contactNumber");
		String location = request.getParameter("location");
		String fromCallBackDate = request.getParameter("fromCallBackDate");
		String toCallBackDate = request.getParameter("toCallBackDate");
		String source = request.getParameter("source");
		String salesTurnoverFrom = request.getParameter("salesTurnoverFrom");
		String salesTurnoverTo = request.getParameter("salesTurnoverTo");
		String tallyUserType = request.getParameter("tallyUserType");
		String leadStatus = request.getParameter("leadStatus");
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
		String noOfDaysSinceLastCall = request.getParameter("noOfDaysSinceLastCall");
		String virtualTeamId = request.getParameter("virtualTeamId");
		
		List<String> RMRoleList = UtilCommon.getArrayToList(request.getParameter("RMRoleList"));
		
		Map < String, Object > returnMap = FastMap.newInstance();
		List < Object > findList = FastList.newInstance();
		Debug.log("==RMRoleList===="+RMRoleList);
		
		try {
			
			String sortDir = "";
			String orderField = "";
			String orderColumnId = request.getParameter("order[0][column]");
			
			if(UtilValidate.isNotEmpty(orderColumnId)) {
				int sortColumnId = Integer.parseInt(orderColumnId);
				String sortColumnName = request.getParameter("columns["+sortColumnId+"][data]");
				sortDir = request.getParameter("order[0][dir]").toUpperCase();
				orderField = sortColumnName;
				
				if (sortColumnId == 11) {
					sortDir = "DESC";
					orderField = "createdTxStamp";
				}
				
			} else {
				orderField = "firstName";
			}
			
			List < String > partyIdsTo = new LinkedList<String>();
			if(UtilValidate.isNotEmpty(RMRoleList)) {
				for(String partyIdTo : RMRoleList) {
					Debug.log("==partyIdTo===="+partyIdTo);
					EntityCondition searchConditions = EntityCondition.makeCondition(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyIdTo),
							EntityUtil.getFilterByDateExpr());
					List<GenericValue> existingRelationship = delegator.findList("PartyRelationship", searchConditions,null, null, null, false);
					if(UtilValidate.isNotEmpty(existingRelationship)) {
					   for(GenericValue partyRelation : existingRelationship) {
					     String partyIds = partyRelation.getString("partyIdFrom");
					     Debug.log("==partyIds===="+partyIds);
						 partyIdsTo.add(partyIds);
					   }
					}
					
				}
			}
			
			Debug.log("==partyIdsTo===="+partyIdsTo);
			
			List < EntityCondition > conditions = new ArrayList < EntityCondition > ();
	
			// construct role conditions
			EntityCondition roleTypeCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "LEAD"),
					EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "LEAD_CONVERTED")), EntityOperator.AND);
			conditions.add(roleTypeCondition);
	
			EntityCondition partyStatusCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
					EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)), EntityOperator.OR);
	
			conditions.add(partyStatusCondition);
			conditions.add(EntityUtil.getFilterByDateExpr());
			if(UtilValidate.isNotEmpty(RMRoleList)) {
				EntityCondition teamMemberCondition = EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIdsTo);
				conditions.add(teamMemberCondition);
			}
	
			if (UtilValidate.isNotEmpty(partyId)) {
				EntityCondition partyCondition = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
				conditions.add(partyCondition);
			}
			if (UtilValidate.isNotEmpty(firstName)) {
				EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(
						EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("firstName")), EntityOperator.LIKE, "%"+firstName.toUpperCase()+"%"),
						EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("partyFirstName")), EntityOperator.LIKE, "%"+firstName.toUpperCase()+"%")
						), EntityOperator.OR);
				//EntityCondition firstNameCondition = EntityCondition.makeCondition("firstName", EntityOperator.LIKE, firstName + "%");
				conditions.add(condition);
			}
			if (UtilValidate.isNotEmpty(lastName)) {
				EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(
						EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("lastName")), EntityOperator.LIKE, "%"+lastName.toUpperCase()+"%"),
						EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("partyLastName")), EntityOperator.LIKE, "%"+lastName.toUpperCase()+"%")
						), EntityOperator.OR);
				//EntityCondition lastNameCondition = EntityCondition.makeCondition("lastName", EntityOperator.LIKE, lastName + "%");
				conditions.add(condition);
			}
			if (UtilValidate.isNotEmpty(companyName)) {
				EntityCondition companyNameCondition = EntityCondition.makeCondition("companyName", EntityOperator.LIKE, "%"+companyName + "%");
				conditions.add(companyNameCondition);
			}
	
			if (UtilValidate.isNotEmpty(source)) {
				EntityCondition condition = EntityCondition.makeCondition("source", EntityOperator.EQUALS, source);
				conditions.add(condition);
			}
			
			if (UtilValidate.isNotEmpty(leadStatus)) {
				EntityCondition condition = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, leadStatus);
				conditions.add(condition);
			}
	
			if (UtilValidate.isNotEmpty(salesTurnoverFrom)) {
				EntityCondition condition = EntityCondition.makeCondition("salesTurnover", EntityOperator.GREATER_THAN_EQUAL_TO, new BigDecimal(salesTurnoverFrom));
				conditions.add(condition);
			}
			if (UtilValidate.isNotEmpty(salesTurnoverTo)) {
				EntityCondition condition = EntityCondition.makeCondition("salesTurnover", EntityOperator.LESS_THAN_EQUAL_TO, new BigDecimal(salesTurnoverTo));
				conditions.add(condition);
			}
	
			if (UtilValidate.isNotEmpty(tallyUserType)) {
				EntityCondition condition = EntityCondition.makeCondition("tallyUserType", EntityOperator.EQUALS, tallyUserType);
				conditions.add(condition);
			}
	
			List < EntityCondition > eventExprs = new LinkedList < EntityCondition > ();
			if (UtilValidate.isNotEmpty(emailAddress) || UtilValidate.isNotEmpty(contactNumber) || UtilValidate.isNotEmpty(location)) {
	
				if (UtilValidate.isNotEmpty(emailAddress)) {
					EntityCondition emailCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("infoString", EntityOperator.EQUALS, emailAddress),
							EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "EMAIL_ADDRESS")), EntityOperator.AND);
					eventExprs.add(emailCondition);
				}
	
				if (UtilValidate.isNotEmpty(contactNumber)) {
					EntityCondition phoneCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("contactNumber", EntityOperator.EQUALS, contactNumber),
							EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "TELECOM_NUMBER")), EntityOperator.AND);
					eventExprs.add(phoneCondition);
				}
	
				if (UtilValidate.isNotEmpty(location)) {
					
					EntityCondition locationCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("city", EntityOperator.EQUALS, location)
							//EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "POSTAL_ADDRESS")
							), EntityOperator.AND);
					eventExprs.add(locationCondition);
					
					/*EntityCondition locationCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("city", EntityOperator.EQUALS, location),
							EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "POSTAL_ADDRESS")), EntityOperator.AND);
					eventExprs.add(locationCondition);*/
				}
				conditions.add(EntityCondition.makeCondition(eventExprs, EntityOperator.OR));
			}
	
			if (UtilValidate.isNotEmpty(fromCallBackDate) || UtilValidate.isNotEmpty(toCallBackDate)) {
				if(UtilValidate.isEmpty(fromCallBackDate)) {
					conditions.add(EntityCondition.makeCondition("lastCallBackDate", EntityOperator.LESS_THAN_EQUAL_TO, java.sql.Date.valueOf(toCallBackDate)));
				} else if(UtilValidate.isEmpty(toCallBackDate)) {
					conditions.add(EntityCondition.makeCondition("lastCallBackDate", EntityOperator.GREATER_THAN_EQUAL_TO, java.sql.Date.valueOf(fromCallBackDate)));
				} else if(UtilValidate.isNotEmpty(fromCallBackDate) && UtilValidate.isNotEmpty(toCallBackDate)) {
					conditions.add(EntityCondition.makeCondition("lastCallBackDate", EntityOperator.BETWEEN, UtilMisc.toList(java.sql.Date.valueOf(fromCallBackDate),java.sql.Date.valueOf(toCallBackDate))));
				}
			}
			if (UtilValidate.isNotEmpty(noOfDaysSinceLastCall)) {
                Integer i = Integer.valueOf(noOfDaysSinceLastCall);
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.DATE, -i);
                String sinceCallDate = formatter1.format(cal.getTime());
                conditions.add(EntityCondition.makeCondition("lastContactDate", EntityOperator.EQUALS, java.sql.Date.valueOf(sinceCallDate)));
            }
			
			//Login Based lead Filter
            String userLoginId = userLogin.getString("userLoginId");
            
            /*List<GenericValue> userLoginSecurityGroup = EntityQuery.use(delegator).from("UserLoginSecurityGroup")
            		.where("userLoginId", userLoginId, "groupId", "FULLADMIN").filterByDate().queryList();
            if(userLoginSecurityGroup == null || userLoginSecurityGroup.size() < 1) {*/
            
            if (LoginFilterUtil.checkEmployeePosition(delegator, userLoginId)) {
            	
				Map<String, Object> dataSecurityMetaInfo = (Map<String, Object>) session.getAttribute("dataSecurityMetaInfo");
				if (ResponseUtils.isSuccess(dataSecurityMetaInfo)) {
		
					List<String> lowerPositionPartyIds = (List<String>) dataSecurityMetaInfo.get("lowerPositionPartyIds");
					if (UtilValidate.isNotEmpty(lowerPositionPartyIds)) {
						
						List < EntityCondition > securityConditions = new ArrayList < EntityCondition > ();
						
						Map<String, Object> virtualTeam = VirtualTeamUtil.getFirstVirtualTeamMember(delegator, virtualTeamId, userLogin.getString("partyId"));
						
						if (UtilValidate.isEmpty(virtualTeam.get("virtualTeamId"))) {
							securityConditions.add(EntityCondition.makeCondition(EntityOperator.AND,
									EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, lowerPositionPartyIds),
									EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
									EntityUtil.getFilterByDateExpr()
									));
						}
						
						if (UtilValidate.isNotEmpty(userLogin)) {
							securityConditions.add(EntityCondition.makeCondition(UtilMisc.toList(
									EntityCondition.makeCondition("uploadedByUserLoginId", EntityOperator.EQUALS, userLogin.getString("userLoginId"))
									//securityConditions
								), EntityOperator.OR));
						}
						
						// virtual team [start]
						
						virtualTeamId = UtilValidate.isNotEmpty(virtualTeamId) ? virtualTeamId : (String) virtualTeam.get("virtualTeamId");
						//String loggedUserVirtualTeamId = (String) dataSecurityMetaInfo.get("loggedUserVirtualTeamId");
						//virtualTeamId = UtilValidate.isNotEmpty(virtualTeamId) ? virtualTeamId : loggedUserVirtualTeamId;
						List<Map<String, Object>> virtualTeamMemberList = VirtualTeamUtil.getVirtualTeamMemberList(delegator, null, userLogin.getString("partyId"));
						if (UtilValidate.isNotEmpty(virtualTeamMemberList)) {
							
							Set<String> virtualTeamIdAsLeadList = VirtualTeamUtil.getVirtualTeamIds(virtualTeamMemberList, "VT_SG_TL", true);
							if (UtilValidate.isNotEmpty(virtualTeamIdAsLeadList)) {
								securityConditions.add(EntityCondition.makeCondition("virtualTeamId", EntityOperator.IN, virtualTeamIdAsLeadList));
								Set<String> virtualTeamMemberPartyIdList = new HashSet<String>();
								for (String vtId : virtualTeamIdAsLeadList) {
									List<Map<String, Object>> teamMemberList = VirtualTeamUtil.getVirtualTeamMemberList(delegator, vtId, null);
									virtualTeamMemberPartyIdList.addAll( DataUtil.getFieldListFromMapList(teamMemberList, "virtualTeamMemberId", true) );
								}
								
								if (UtilValidate.isNotEmpty(virtualTeamMemberPartyIdList)) {
									securityConditions.add(EntityCondition.makeCondition(EntityOperator.AND,
											EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, virtualTeamMemberPartyIdList),
											EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
											EntityUtil.getFilterByDateExpr()
											));
								}
								
							}
							
							Set<String> virtualTeamIdAsMemberList = VirtualTeamUtil.getVirtualTeamIds(virtualTeamMemberList, "VT_SG_TL", false);
							if (UtilValidate.isNotEmpty(virtualTeamIdAsMemberList)) {
								securityConditions.add(EntityCondition.makeCondition(UtilMisc.toList(
										EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userLogin.getString("partyId")),
										EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
										//EntityCondition.makeCondition("virtualTeamId", EntityOperator.IN, virtualTeamIdAsMemberList),
										EntityUtil.getFilterByDateExpr()
									), EntityOperator.AND));
								
							}
							
						}
						
						// virtual team [end]
		
						EntityCondition securityCondition = EntityCondition.makeCondition(UtilMisc.toList(
								securityConditions
								), EntityOperator.OR);
						
						conditions.add(securityCondition);
					}
		
					Debug.log("lowerPositionPartyIds> "+lowerPositionPartyIds);
		
				}
				
            }
            
			EntityCondition orderFieldConditions = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition(orderField, EntityOperator.NOT_EQUAL,null),
					EntityUtil.getFilterByDateExpr()
					);
			
			conditions.add(orderFieldConditions);
			EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			
			EntityFindOptions efo = new EntityFindOptions();
			efo.setDistinct(true);
			efo.getDistinct();
			
			int startInx = UtilValidate.isNotEmpty(start) ? Integer.parseInt(start) : 0;
			int endInx = UtilValidate.isNotEmpty(length) ? Integer.parseInt(length) : 0;
			
			efo.setOffset(startInx);
			efo.setLimit(endInx);
			
			long count = 0;
			
			EntityFindOptions  efoNum= new EntityFindOptions();
			
			efoNum.setDistinct(true);
			efoNum.getDistinct();

			efoNum.setFetchSize(1000);
			
			Debug.logInfo("count 1 start: "+UtilDateTime.nowTimestamp(), MODULE);
			count = delegator.findCountByCondition("LeadSummaryView", mainConditons, null, UtilMisc.toSet("partyId"), efoNum);
			Debug.logInfo("count 2 end: "+UtilDateTime.nowTimestamp(), MODULE);
			
			/*List < GenericValue > partiesList = delegator.findList("PartyFromByRelnAndContactInfoAndPartySupplemantalData", mainConditons, UtilMisc.toSet("partyId"), UtilMisc.toList(orderField+ " " + sortDir), null, false);
			if (partiesList != null && partiesList.size() > 0) {
				count = EntityUtil.getFieldListFromEntityList(partiesList, "partyId", true).size();
			}
			if (count > 1000) {
				count = 1000;
			}*/
			
			long recordsFiltered = count;
			long recordsTotal = count;
			
			//List<GenericValue> parties = delegator.findList("PartyFromByRelnAndContactInfoAndPartySupplemantalData", mainConditons, UtilMisc.toSet("partyId"), UtilMisc.toList(orderField+ " " + sortDir), efo, false);
			Debug.logInfo("list 1 start: "+UtilDateTime.nowTimestamp(), MODULE);
			List<GenericValue> parties = delegator.findList("LeadSummaryView", mainConditons, UtilMisc.toSet("partyId"), UtilMisc.toList(orderField+ " " + sortDir), efo, false);
			Debug.logInfo("list 2 start: "+UtilDateTime.nowTimestamp(), MODULE);
			
			if (parties != null && parties.size() > 0) {
				List < String > partyIdList = EntityUtil.getFieldListFromEntityList(parties, "partyId", true);
				if (partyIdList != null && partyIdList.size() > 0) {
					for (String leadId: partyIdList) {
						String callBackDate = "";
						String diffDays = "";
						String lastContactDate ="";
						/*GenericValue partyNoteView = EntityUtil.getFirst(delegator.findByAnd("PartyNoteView", UtilMisc.toMap("targetPartyId", leadId), UtilMisc.toList("noteDateTime DESC"), false));
						if (partyNoteView != null && partyNoteView.size() > 0) {
							String noteId = partyNoteView.getString("noteId");
							GenericValue noteData = delegator.findOne("NoteData", UtilMisc.toMap("noteId", noteId), false);
							if (noteData != null && noteData.size() > 0) {
								callBackDate = noteData.getString("callBackDate");
							}
						}*/
						GenericValue partySummaryDetailsView = delegator.findOne("PartySummaryDetailsView", UtilMisc.toMap("partyId", leadId), false);
						if (partySummaryDetailsView != null && partySummaryDetailsView.size() > 0) {
							Map< String, Object > partyDetails = new HashMap< String, Object >();
							//String callBackDate = partySummaryDetailsView.getString("callBackDate");
							String companyNameDetails = partySummaryDetailsView.getString("companyName");
							String statusId = partySummaryDetailsView.getString("statusId");
							String statusItemDesc = "";
							String name = partySummaryDetailsView.getString("firstName");
							if (UtilValidate.isNotEmpty(partySummaryDetailsView.getString("lastName"))) {
								name = name + " " + partySummaryDetailsView.getString("lastName");
							}
							if(UtilValidate.isEmpty(name)) {
								name = partySummaryDetailsView.getString("groupName");
							}
							if (UtilValidate.isNotEmpty(statusId)) {
								GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
								if (statusItem != null && statusItem.size() > 0) {
									statusItemDesc = statusItem.getString("description");
								}
							}
	
							String dataSourceDesc = partySummaryDetailsView.getString("source");
	
							/* GenericValue partyDataSource = EntityUtil.getFirst(delegator.findByAnd("PartyDataSource", UtilMisc.toMap("partyId", leadId), UtilMisc.toList("fromDate DESC"), false));
	                        if (partyDataSource != null && partyDataSource.size() > 0) {
	                            String dataSourceId = partyDataSource.getString("dataSourceId");
	                            if (UtilValidate.isNotEmpty(dataSourceId)) {
	                                GenericValue dataSource = delegator.findOne("DataSource", UtilMisc.toMap("dataSourceId", dataSourceId), false);
	                                if (dataSource != null && dataSource.size() > 0) {
	                                    dataSourceDesc = dataSource.getString("description");
	                                }
	                            }
	                        }*/
	
							String phoneNumber = "";
							String infoString = "";
							String city = "";
							String state = "";
							List < GenericValue > partyContactMechs = delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", leadId, "allowSolicitation", "Y"), null, false);
							if (partyContactMechs != null && partyContactMechs.size() > 0) {
								partyContactMechs = EntityUtil.filterByDate(partyContactMechs);
								if (partyContactMechs != null && partyContactMechs.size() > 0) {
									partyContactMechs = EntityUtil.getFieldListFromEntityList(partyContactMechs, "contactMechId", true);
								}
								if (partyContactMechs != null && partyContactMechs.size() > 0) {
									Set < String > findOptions = UtilMisc.toSet("contactMechId");
									List < String > orderBy = UtilMisc.toList("createdStamp DESC");
	
									EntityCondition condition1 = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, leadId);
									EntityCondition condition2 = EntityCondition.makeCondition("contactMechId", EntityOperator.IN, partyContactMechs);
	
									EntityCondition primaryPhoneConditions = EntityCondition.makeCondition(UtilMisc.toList(condition1, condition2, EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_PHONE")));
									List < GenericValue > primaryPhones = delegator.findList("PartyContactMechPurpose", primaryPhoneConditions, findOptions, null, null, false);
									if (primaryPhones != null && primaryPhones.size() > 0) {
										GenericValue primaryPhone = EntityUtil.getFirst(EntityUtil.filterByDate(primaryPhones));
										if (UtilValidate.isNotEmpty(primaryPhone)) {
											GenericValue primaryPhoneNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", primaryPhone.getString("contactMechId")), false);
											if (UtilValidate.isNotEmpty(primaryPhoneNumber)) {
												phoneNumber = primaryPhoneNumber.getString("contactNumber");
											}
										}
									}
	
									EntityCondition primaryEmailConditions = EntityCondition.makeCondition(UtilMisc.toList(condition1, condition2, EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_EMAIL")));
									List < GenericValue > primaryEmails = delegator.findList("PartyContactMechPurpose", primaryEmailConditions, findOptions, null, null, false);
									if (primaryEmails != null && primaryEmails.size() > 0) {
										GenericValue primaryEmail = EntityUtil.getFirst(EntityUtil.filterByDate(primaryEmails));
										if (UtilValidate.isNotEmpty(primaryEmail)) {
											GenericValue primaryInfoString = delegator.findOne("ContactMech", UtilMisc.toMap("contactMechId", primaryEmail.getString("contactMechId")), false);
											if (UtilValidate.isNotEmpty(primaryInfoString)) {
												infoString = primaryInfoString.getString("infoString");
											}
										}
									}
	
									EntityCondition postalAddressConditions = EntityCondition.makeCondition(UtilMisc.toList(condition1, condition2, EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_LOCATION")));
									List < GenericValue > primaryAddressList = delegator.findList("PartyContactMechPurpose", postalAddressConditions, findOptions, null, null, false);
									if (primaryAddressList != null && primaryAddressList.size() > 0) {
										GenericValue primaryAddress = EntityUtil.getFirst(EntityUtil.filterByDate(primaryAddressList));
										if (UtilValidate.isNotEmpty(primaryAddress)) {
											GenericValue postalAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", primaryAddress.getString("contactMechId")), false);
											if (UtilValidate.isNotEmpty(postalAddress)) {
												city = postalAddress.getString("city");
												String stateProvinceGeoId = postalAddress.getString("stateProvinceGeoId");
												if (UtilValidate.isNotEmpty(stateProvinceGeoId)) {
													GenericValue geo = delegator.findOne("Geo", UtilMisc.toMap("geoId", stateProvinceGeoId), false);
													if (UtilValidate.isNotEmpty(geo)) {
														state = geo.getString("geoName");
													}
												}
											}
										}
									}
								}
							}
	
							// Person Responsible for  [start]
	
							String personResponsible = "";
							String personResponsibleAssignBy = "";
	
							if (UtilValidate.isNotEmpty(leadId)) {
								EntityCondition conditionPR = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, leadId),
										EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT_MANAGER"),
										EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, UtilMisc.toList("ACCOUNT", "LEAD", "CONTACT")),
										EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
										EntityUtil.getFilterByDateExpr()), EntityOperator.AND);
	
								GenericValue responsibleFor = EntityQuery.use(delegator).from("PartyRelationship").where(conditionPR).orderBy("fromDate DESC").queryFirst();
	
								if (UtilValidate.isNotEmpty(responsibleFor)) {
									String partyIdTo = responsibleFor.getString("partyIdTo");
									personResponsible = PartyHelper.getPartyName(delegator, partyIdTo, false);
	
									if (UtilValidate.isNotEmpty(responsibleFor.getString("createdByUserLoginId"))) {
										GenericValue createdByUserLogin = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", responsibleFor.getString("createdByUserLoginId")).queryFirst();
										if (UtilValidate.isNotEmpty(createdByUserLogin)) {
											personResponsibleAssignBy = PartyHelper.getPartyName(delegator, createdByUserLogin.getString("partyId"), false);
										}
									}
	
								}
	
							}
	
							String segment = "";
							String liabOrAsset = "";
							String teleCallingStatus = "";
							leadStatus = "";
							GenericValue partySupplementalData = EntityUtil.getFirst( delegator.findByAnd("PartySupplementalData", UtilMisc.toMap("partyId", leadId), null, false) );
							if (UtilValidate.isNotEmpty(partySupplementalData)) {
								segment = partySupplementalData.getString("segment");
								if (UtilValidate.isNotEmpty(segment)) {
									GenericValue seg = EntityUtil.getFirst( delegator.findByAnd("CustomField", UtilMisc.toMap("customFieldId", segment), null, false) );
									if (UtilValidate.isNotEmpty(seg)) {
										segment = seg.getString("customFieldName");
									}
								}
	
								liabOrAsset = partySupplementalData.getString("liabOrAsset");													
								if (UtilValidate.isNotEmpty(liabOrAsset)) {
									GenericValue validEnum = ValidatorUtil.getValidEnum(delegator, partySupplementalData.getString("liabOrAsset"));
									if (UtilValidate.isNotEmpty(validEnum)) {
										liabOrAsset = validEnum.getString("description");
									}
								}
	
								GenericValue leadSource = EntityUtil.getFirst( delegator.findByAnd("PartyIdentificationType", UtilMisc.toMap("partyIdentificationTypeId", partySupplementalData.getString("source")), null, false) );
								if (UtilValidate.isNotEmpty(leadSource)) {
									dataSourceDesc = "("+leadSource.getString("partyIdentificationTypeId")+") "+leadSource.getString("description");
								}
	
								if (UtilValidate.isNotEmpty(partySupplementalData.getString("teleCallingStatus"))) {
									GenericValue enumeration = EntityUtil.getFirst( delegator.findByAnd("Enumeration", UtilMisc.toMap("enumId", partySupplementalData.getString("teleCallingStatus")), null, false) );
									if (UtilValidate.isNotEmpty(enumeration)) {
										teleCallingStatus = enumeration.getString("description");
									}
								}
								
								/*if (UtilValidate.isNotEmpty(partySupplementalData.getString("leadStatus"))) {
									GenericValue enumeration = EntityUtil.getFirst( delegator.findByAnd("Enumeration", UtilMisc.toMap("enumId", partySupplementalData.getString("leadStatus")), null, false) );
									if (UtilValidate.isNotEmpty(enumeration)) {
										leadStatus = enumeration.getString("description");
									}
								}*/
								
								SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
								callBackDate = partySupplementalData.getString("lastCallBackDate");
								lastContactDate = UtilValidate.isNotEmpty(partySupplementalData.getString("lastContactDate")) ? partySupplementalData.getString("lastContactDate") : "";
								if (UtilValidate.isNotEmpty(lastContactDate)) {
			                        String currentDate = formatter.format(new Date());
			                        Date d1 = new SimpleDateFormat("yyyy-MM-dd").parse(lastContactDate);
			                        Date d2 = formatter.parse(currentDate);
			                        long diff = d2.getTime() - d1.getTime();
			                        long diffDay = diff / (24 * 60 * 60 * 1000);
			                        diffDays = String.valueOf(diffDay);
			                    }
								if(UtilValidate.isNotEmpty(callBackDate)) {
								  Date callBackDate1 = new SimpleDateFormat("yyyy-MM-dd").parse(callBackDate);
		                          callBackDate = sdf.format(callBackDate1);
								}
								
								partyDetails.put("createdStamp", UtilValidate.isNotEmpty(partySupplementalData.get("createdStamp")) ? UtilDateTime.timeStampToString(partySupplementalData.getTimestamp("createdStamp"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
	
							}
							
							GenericValue entLeadStatus = EntityUtil.getFirst( delegator.findByAnd("LeadStatus", UtilMisc.toMap("leadId", leadId), null, false) );
							if (UtilValidate.isNotEmpty(entLeadStatus)) {
								if (UtilValidate.isNotEmpty(entLeadStatus.getString("statusId"))) {
									GenericValue enumeration = EntityUtil.getFirst( delegator.findByAnd("Enumeration", UtilMisc.toMap("enumCode", entLeadStatus.getString("statusId")), null, false) );
									if (UtilValidate.isNotEmpty(enumeration)) {
										leadStatus = enumeration.getString("description");
									}
								}
							}
							
	
							// Person Responsible for  [end]
	
							partyDetails.put("partyId", leadId);
							partyDetails.put("name", name);
							partyDetails.put("firstName", UtilValidate.isNotEmpty(partySummaryDetailsView.getString("firstName")) ? partySummaryDetailsView.getString("firstName") : partySummaryDetailsView.getString("partyFirstName"));
							partyDetails.put("callBackDate", callBackDate);
							partyDetails.put("companyName", companyNameDetails);
							partyDetails.put("statusDescription", statusItemDesc);
							partyDetails.put("dataSourceDesc", dataSourceDesc);
							partyDetails.put("contactNumber", phoneNumber);
							partyDetails.put("emailAddress", infoString);
							partyDetails.put("city", city);
							partyDetails.put("state", state);
	
							partyDetails.put("personResponsible", personResponsible);
							partyDetails.put("personResponsibleAssignBy", personResponsibleAssignBy);
							partyDetails.put("segment", segment);
							partyDetails.put("liabOrAsset", liabOrAsset);
							partyDetails.put("teleCallingStatus", teleCallingStatus);
							partyDetails.put("leadStatus", leadStatus);
							partyDetails.put("diffDays", diffDays);
							partyDetails.put("noOfDateSinceLastCall", lastContactDate);
	
							findList.add(partyDetails);
						}
					}
				}
				returnMap.put("data", findList);
				returnMap.put("draw", draw);
				returnMap.put("recordsTotal", recordsTotal);
				returnMap.put("recordsFiltered", recordsFiltered);
			} else {
				returnMap.put("data", findList);
				returnMap.put("draw", draw);
				returnMap.put("recordsTotal", 0);
				returnMap.put("recordsFiltered", 0);
				return AjaxEvents.doJSONResponse(response, returnMap);
			}
		} catch (Exception e) {
			Debug.logError("Exception in Get Account Details" + e.getMessage(), MODULE);
			returnMap.put("data", findList);
			returnMap.put("draw", draw);
			returnMap.put("recordsTotal", 0);
			returnMap.put("recordsFiltered", 0);
			return AjaxEvents.doJSONResponse(response, returnMap);
		}
		return AjaxEvents.doJSONResponse(response, returnMap);
	}
	public static String getLeadDetailsExt(HttpServletRequest request, HttpServletResponse response) {
	
		HttpSession session = request.getSession();
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        Map<String, Object> context = UtilHttp.getCombinedMap(request);
		
		String partyId = request.getParameter("partyId");
		String companyName = request.getParameter("companyName");
		String location = request.getParameter("location");
		String leadStatus = request.getParameter("leadStatus");
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
		String virtualTeamId = ""; //request.getParameter("virtualTeamId");
		String source = request.getParameter("source");
		String leadSubStatusInp = request.getParameter("leadSubStatus");
		String leadAssignTo = request.getParameter("leadAssignTo");
		String userManager = request.getParameter("userManager");
		
		List<String> RMRoleList = UtilCommon.getArrayToList(request.getParameter("RMRoleList"));
		
		/*Map < String, Object > returnMap = FastMap.newInstance();
		List < Object > findList = FastList.newInstance();*/
		Debug.log("==RMRoleList===="+RMRoleList);
		
		try {
			List < String > partyIdsTo = new LinkedList<String>();
			if(UtilValidate.isNotEmpty(RMRoleList)) {
				for(String partyIdTo : RMRoleList) {
					Debug.log("==partyIdTo===="+partyIdTo);
					EntityCondition searchConditions = EntityCondition.makeCondition(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyIdTo),
							EntityUtil.getFilterByDateExpr());
					List<GenericValue> existingRelationship = delegator.findList("PartyRelationship", searchConditions,null, null, null, false);
					if(UtilValidate.isNotEmpty(existingRelationship)) {
					   for(GenericValue partyRelation : existingRelationship) {
					     String partyIds = partyRelation.getString("partyIdFrom");
					     Debug.log("==partyIds===="+partyIds);
						 partyIdsTo.add(partyIds);
					   }
					}
					
				}
			}
			
			Debug.log("==partyIdsTo===="+partyIdsTo);
			
			List < EntityCondition > conditions = new ArrayList < EntityCondition > ();
	
			// construct role conditions
			EntityCondition roleTypeCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "LEAD"),
					EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "LEAD_CONVERTED")), EntityOperator.AND);
			conditions.add(roleTypeCondition);
	
			EntityCondition partyStatusCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
					EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)), EntityOperator.OR);
	
			conditions.add(partyStatusCondition);
			conditions.add(EntityUtil.getFilterByDateExpr());
			if(UtilValidate.isNotEmpty(RMRoleList)) {
				EntityCondition teamMemberCondition = EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIdsTo);
				conditions.add(teamMemberCondition);
			}
	
			if (UtilValidate.isNotEmpty(partyId)) {
				EntityCondition partyCondition = EntityCondition.makeCondition("partyId", EntityOperator.LIKE, "%" + partyId + "%");
				conditions.add(partyCondition);
			}
			if (UtilValidate.isNotEmpty(companyName)) {
				EntityCondition companyNameCondition = EntityCondition.makeCondition("companyName", EntityOperator.LIKE, "%"+companyName + "%");
				conditions.add(companyNameCondition);
			}
			
			/*if (UtilValidate.isNotEmpty(source)) {
				List<String> sourceList = UtilCommon.getArrayToList(source);
				EntityCondition condition = EntityCondition.makeCondition("source", EntityOperator.IN, sourceList);
				conditions.add(condition);
			}*/
	
			if (UtilValidate.isNotEmpty(leadStatus)) {
				List<String> leadStatusList = UtilCommon.getArrayToList(leadStatus);
				EntityCondition condition = EntityCondition.makeCondition("statusId", EntityOperator.IN, leadStatusList);
				conditions.add(condition);
			}
			/*if (UtilValidate.isNotEmpty(leadSubStatus)) {
				List<String> leadSubStatusList = UtilCommon.getArrayToList(leadSubStatus);
				EntityCondition condition = EntityCondition.makeCondition("outcomeId", EntityOperator.IN, leadSubStatusList);
				conditions.add(condition);
			}*/
			if (UtilValidate.isNotEmpty(location)) {
				List<String> locationList = UtilCommon.getArrayToList(location);System.out.println("\nABCD\n" + locationList + "\nBVCD\n");
				EntityCondition locationCondition = EntityCondition.makeCondition("paCity", EntityOperator.IN, locationList);
				conditions.add(locationCondition);
			}
			if (UtilValidate.isNotEmpty(leadAssignTo)) {
				List<String> leadAssignToList = UtilCommon.getArrayToList(leadAssignTo);
				EntityCondition condition = EntityCondition.makeCondition("leadAssignTo", EntityOperator.IN, leadAssignToList);
				conditions.add(condition);
			}/*else{
				if(!"admin".equalsIgnoreCase(userLogin.getString("userLoginId"))) {
					List<GenericValue> aoRoles = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", userLogin.getString("partyId"), "roleTypeId", "DBS_CENTRAL"), null, false);
					Debug.log("====aoRoles==="+aoRoles.size());
					if(UtilValidate.isEmpty(aoRoles)) {
						EntityCondition condition = EntityCondition.makeCondition("leadAssignTo", EntityOperator.EQUALS, userLogin.getString("partyId"));
						conditions.add(condition);
					}
				}
			}*/
			//Login Based lead Filter
            String userLoginId = userLogin.getString("userLoginId");
            
            /*List<GenericValue> userLoginSecurityGroup = EntityQuery.use(delegator).from("UserLoginSecurityGroup")
            		.where("userLoginId", userLoginId, "groupId", "FULLADMIN").filterByDate().queryList();
            if(userLoginSecurityGroup == null || userLoginSecurityGroup.size() < 1) {*/
            List<String> multiTeamLists = new ArrayList<String>();
            Boolean  isTeamLead = true; 
            if (LoginFilterUtil.checkEmployeePosition(delegator, userLoginId)) {
            	
            	Map<String, Object> dataSecurityMetaInfo = (Map<String, Object>) session.getAttribute("dataSecurityMetaInfo");
				if (ResponseUtils.isSuccess(dataSecurityMetaInfo)) {
		
					List<String> lowerPositionPartyIds = (List<String>) dataSecurityMetaInfo.get("lowerPositionPartyIds");
					if (UtilValidate.isNotEmpty(lowerPositionPartyIds)) {
						
						List < EntityCondition > securityConditions = new ArrayList < EntityCondition > ();
						
						Map<String, Object> virtualTeam = VirtualTeamUtil.getFirstVirtualTeamMember(delegator, virtualTeamId, userLogin.getString("partyId"));
						
						if (UtilValidate.isEmpty(virtualTeam.get("virtualTeamId"))) {
							securityConditions.add(EntityCondition.makeCondition(EntityOperator.AND,
									EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, lowerPositionPartyIds),
									EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
									EntityUtil.getFilterByDateExpr()
									));
						}
						
						if (UtilValidate.isNotEmpty(userLogin)) {
							securityConditions.add(EntityCondition.makeCondition(UtilMisc.toList(
									EntityCondition.makeCondition("uploadedByUserLoginId", EntityOperator.EQUALS, userLogin.getString("userLoginId"))
									//securityConditions
								), EntityOperator.OR));
						}
						
						// virtual team [start]
						securityConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("leadAssignTo", EntityOperator.EQUALS, userLogin.getString("partyId"))));
						virtualTeamId = UtilValidate.isNotEmpty(virtualTeamId) ? virtualTeamId : (String) virtualTeam.get("virtualTeamId");
						
						//String loggedUserVirtualTeamId = (String) dataSecurityMetaInfo.get("loggedUserVirtualTeamId");
						//virtualTeamId = UtilValidate.isNotEmpty(virtualTeamId) ? virtualTeamId : loggedUserVirtualTeamId;
						List<Map<String, Object>> virtualTeamMemberList = VirtualTeamUtil.getVirtualTeamMemberList(delegator, null, userLogin.getString("partyId"));
						if (UtilValidate.isNotEmpty(virtualTeamMemberList)) {
							/*Set<String> virtualTeamIdAsLeadList = VirtualTeamUtil.getVirtualTeamIds(virtualTeamMemberList, "VT_SG_TL", true);*/
							List<String> virtualTeamIdAsLeadList = VirtualTeamUtil.getVirtualTeamIds(delegator,userLogin.getString("partyId"));
							Debug.log("===virtualTeamIdAsLeadList==="+virtualTeamIdAsLeadList);
							if (UtilValidate.isNotEmpty(virtualTeamIdAsLeadList)) {
								List<String> multiTeamMembers = new ArrayList<String>();
								/*securityConditions.add(EntityCondition.makeCondition("virtualTeamId", EntityOperator.IN, virtualTeamIdAsLeadList));*/
								Set<String> virtualTeamMemberPartyIdList = new HashSet<String>();
								for (String vtId : virtualTeamIdAsLeadList) {
									List<String> roles = DataUtil.getLoginRole(delegator, userLogin.getString("partyId"), vtId);
								    if(roles.contains("VT_SG_TL")) {
								    	List<Map<String, Object>> teamMemberList = VirtualTeamUtil.getVirtualTeamMemberList(delegator, vtId, null);
								    	Debug.log("===teamMemberList==="+teamMemberList);
								    	for(Map<String, Object> memberList : teamMemberList) {
								    		multiTeamLists.add((String) memberList.get("virtualTeamMemberId"));
								    	}
								    	virtualTeamMemberPartyIdList.addAll( DataUtil.getFieldListFromMapList(teamMemberList, "virtualTeamMemberId", true) );
								    	multiTeamMembers.add(vtId);
								    }
								}
								Debug.log("===multiTeamMembers==="+multiTeamMembers+"==multiTeamLists==="+multiTeamLists);
								securityConditions.add(EntityCondition.makeCondition("virtualTeamId", EntityOperator.IN, multiTeamMembers));
								if (UtilValidate.isNotEmpty(virtualTeamMemberPartyIdList)) {
									securityConditions.add(EntityCondition.makeCondition(EntityOperator.AND,
											EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, virtualTeamMemberPartyIdList),
											EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
											EntityUtil.getFilterByDateExpr()
											));
								}
							}
							
							Set<String> virtualTeamIdAsMemberList = VirtualTeamUtil.getVirtualTeamIds(virtualTeamMemberList, "VT_SG_TL", false);
							if (UtilValidate.isNotEmpty(virtualTeamIdAsMemberList)) {
								securityConditions.add(EntityCondition.makeCondition(UtilMisc.toList(
										EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userLogin.getString("partyId")),
										EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
										//EntityCondition.makeCondition("virtualTeamId", EntityOperator.IN, virtualTeamIdAsMemberList),
										EntityUtil.getFilterByDateExpr()
									), EntityOperator.AND));
							}
							
						}
						
						// virtual team [end]
						
						EntityCondition securityCondition = EntityCondition.makeCondition(UtilMisc.toList(
								securityConditions
								), EntityOperator.OR);
						
						conditions.add(securityCondition);
					}
					EntityCondition searchTlConditions = EntityCondition.makeCondition(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userLogin.getString("partyId")),
							EntityCondition.makeCondition("securityGroupId", EntityOperator.EQUALS, "VT_SG_TL"),
							EntityUtil.getFilterByDateExpr());
					List<GenericValue> tlRelationship = delegator.findList("PartyRelationship", searchTlConditions,null, null, null, false);
					if(UtilValidate.isEmpty(tlRelationship)) {
						if (UtilValidate.isEmpty(leadAssignTo)) {
							if(!"admin".equalsIgnoreCase(userLogin.getString("userLoginId"))) {
								List<GenericValue> aoRoles = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", userLogin.getString("partyId"), "roleTypeId", "DBS_CENTRAL"), null, false);
								Debug.log("====aoRoles==="+aoRoles.size());
								if(UtilValidate.isEmpty(aoRoles)) {
									EntityCondition condition = EntityCondition.makeCondition("leadAssignTo", EntityOperator.EQUALS, userLogin.getString("partyId"));
									conditions.add(condition);
									isTeamLead = false;
								}
							}
						
						}
					}
					Debug.log("lowerPositionPartyIds> "+lowerPositionPartyIds);
				}
				
            }
			/*EntityCondition orderFieldConditions = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition(orderField, EntityOperator.NOT_EQUAL,null),
					EntityUtil.getFilterByDateExpr()
					);
			
			conditions.add(orderFieldConditions);*/
			EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			
			EntityFindOptions efo = new EntityFindOptions();
			efo.setDistinct(true);
			efo.getDistinct();
			
			List<GenericValue> parties = delegator.findList("LeadSummaryView", mainConditons, null, null, efo, false);
						
				for (GenericValue obj: parties) {
					String leadId = obj.getString("partyId");
					java.sql.Timestamp createdTxStamp = (java.sql.Timestamp) obj.getTimestamp("createdTxStamp");
					String callBackDate = "";
					String diffDays = "";
					String lastContactDate ="";
					/*GenericValue partyNoteView = EntityUtil.getFirst(delegator.findByAnd("PartyNoteView", UtilMisc.toMap("targetPartyId", leadId), UtilMisc.toList("noteDateTime DESC"), false));
					if (partyNoteView != null && partyNoteView.size() > 0) {
						String noteId = partyNoteView.getString("noteId");
						GenericValue noteData = delegator.findOne("NoteData", UtilMisc.toMap("noteId", noteId), false);
						if (noteData != null && noteData.size() > 0) {
							callBackDate = noteData.getString("callBackDate");
						}
					}*/
					GenericValue partySummaryDetailsView = delegator.findOne("PartySummaryDetailsView", UtilMisc.toMap("partyId", leadId), false);
					if (partySummaryDetailsView != null && partySummaryDetailsView.size() > 0) {
						Map< String, Object > partyDetails = new HashMap< String, Object >();
						//String callBackDate = partySummaryDetailsView.getString("callBackDate");
						String companyNameDetails = partySummaryDetailsView.getString("companyName");
						String statusId = partySummaryDetailsView.getString("statusId");
						String statusItemDesc = "";
						String name = partySummaryDetailsView.getString("firstName");
						if (UtilValidate.isNotEmpty(partySummaryDetailsView.getString("lastName"))) {
							name = name + " " + partySummaryDetailsView.getString("lastName");
						}
						if(UtilValidate.isEmpty(name)) {
							name = partySummaryDetailsView.getString("groupName");
						}
						if (UtilValidate.isNotEmpty(statusId)) {
							GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
							if (statusItem != null && statusItem.size() > 0) {
								statusItemDesc = statusItem.getString("description");
							}
						}
	
						String dataSourceDesc = null;
						String dataSource = UtilValidate.isNotEmpty(partySummaryDetailsView.getString("createSource")) ? partySummaryDetailsView.getString("createSource") : partySummaryDetailsView.getString("source");
	
						/* GenericValue partyDataSource = EntityUtil.getFirst(delegator.findByAnd("PartyDataSource", UtilMisc.toMap("partyId", leadId), UtilMisc.toList("fromDate DESC"), false));
	                    if (partyDataSource != null && partyDataSource.size() > 0) {
	                        String dataSourceId = partyDataSource.getString("dataSourceId");
	                        if (UtilValidate.isNotEmpty(dataSourceId)) {
	                            GenericValue dataSource = delegator.findOne("DataSource", UtilMisc.toMap("dataSourceId", dataSourceId), false);
	                            if (dataSource != null && dataSource.size() > 0) {
	                                dataSourceDesc = dataSource.getString("description");
	                            }
	                        }
	                    }*/
	
						String phoneNumber = "";
						String infoString = "";
						String city = "";
						String state = "";
						String cityName = "";
						
						EntityCondition postalAddressConditions = EntityCondition.makeCondition(
								UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, leadId), 
										EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_LOCATION")), 
								EntityOperator.AND);
						
							GenericValue primaryAddress = EntityUtil.getFirst(delegator.findList("PartyContactMechPurpose", postalAddressConditions, 
									UtilMisc.toSet("contactMechId"), null, null, false));
							if (UtilValidate.isNotEmpty(primaryAddress)) {
								GenericValue postalAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", primaryAddress.getString("contactMechId")), false);
								if (UtilValidate.isNotEmpty(postalAddress)) {
									city = postalAddress.getString("city");
									if (UtilValidate.isNotEmpty(city)) {
										GenericValue geo = delegator.findOne("Geo", UtilMisc.toMap("geoId", city), false);
										if (UtilValidate.isNotEmpty(geo)) {
											cityName = geo.getString("geoName");
										}
									}
								}
							}
						
						// Person Responsible for  [start]
	
						String personResponsible = "";
						String personResponsibleAssignBy = "";
						int daysInQueue = 0;
						if (UtilValidate.isNotEmpty(leadId)) {
							EntityCondition conditionPR = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, leadId),
									EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT_MANAGER"),
									EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, UtilMisc.toList("ACCOUNT", "LEAD", "CONTACT")),
									EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
									EntityUtil.getFilterByDateExpr()), EntityOperator.AND);
	
							GenericValue responsibleFor = EntityQuery.use(delegator).from("PartyRelationship").where(conditionPR).orderBy("fromDate DESC").queryFirst();
	
							if (UtilValidate.isNotEmpty(responsibleFor)) {
								String partyIdTo = responsibleFor.getString("partyIdTo");
								personResponsible = PartyHelper.getPartyName(delegator, partyIdTo, false);
	
								if (UtilValidate.isNotEmpty(responsibleFor.getString("createdByUserLoginId"))) {
									GenericValue createdByUserLogin = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", responsibleFor.getString("createdByUserLoginId")).queryFirst();
									if (UtilValidate.isNotEmpty(createdByUserLogin)) {
										personResponsibleAssignBy = PartyHelper.getPartyName(delegator, createdByUserLogin.getString("partyId"), false);
									}
								}
								
								if (UtilValidate.isNotEmpty(responsibleFor.getTimestamp("fromDate"))) {
									daysInQueue = UtilDateTime.getIntervalInDays(responsibleFor.getTimestamp("fromDate"), UtilDateTime.nowTimestamp());
			                    }
							}
						}
	
						String segment = "";
						String liabOrAsset = "";
						String teleCallingStatus = "";
						leadStatus = "";
						String leadClassification = "";
						GenericValue partySupplementalData = EntityUtil.getFirst( delegator.findByAnd("PartySupplementalData", UtilMisc.toMap("partyId", leadId), null, false) );
						if (UtilValidate.isNotEmpty(partySupplementalData)) {
							segment = partySupplementalData.getString("segment");
							if (UtilValidate.isNotEmpty(segment)) {
								GenericValue seg = EntityUtil.getFirst( delegator.findByAnd("CustomField", UtilMisc.toMap("customFieldId", segment), null, false) );
								if (UtilValidate.isNotEmpty(seg)) {
									segment = seg.getString("customFieldName");
								}
							}
	
							liabOrAsset = partySupplementalData.getString("liabOrAsset");													
							if (UtilValidate.isNotEmpty(liabOrAsset)) {
								GenericValue validEnum = ValidatorUtil.getValidEnum(delegator, partySupplementalData.getString("liabOrAsset"));
								if (UtilValidate.isNotEmpty(validEnum)) {
									liabOrAsset = validEnum.getString("description");
								}
							}
							if(UtilValidate.isNotEmpty(dataSource)) {
								GenericValue leadSource = EntityUtil.getFirst( delegator.findByAnd("PartyIdentificationType", UtilMisc.toMap("partyIdentificationTypeId", dataSource), null, false) );
								if (UtilValidate.isNotEmpty(leadSource)) {
									dataSourceDesc = "("+leadSource.getString("partyIdentificationTypeId")+") "+leadSource.getString("description");
								}
							}
							if(UtilValidate.isNotEmpty(source)) {
								if(UtilValidate.isNotEmpty(dataSource)) {
									List<String> sourceList = UtilCommon.getArrayToList(source);
									if(!sourceList.contains(dataSource)) {
										continue;
									}
								}
								else {
									continue;
								}
							}
	
							if (UtilValidate.isNotEmpty(partySupplementalData.getString("teleCallingStatus"))) {
								GenericValue enumeration = EntityUtil.getFirst( delegator.findByAnd("Enumeration", UtilMisc.toMap("enumId", partySupplementalData.getString("teleCallingStatus")), null, false) );
								if (UtilValidate.isNotEmpty(enumeration)) {
									teleCallingStatus = enumeration.getString("description");
								}
							}
							
							/*if (UtilValidate.isNotEmpty(partySupplementalData.getString("leadStatus"))) {
								GenericValue enumeration = EntityUtil.getFirst( delegator.findByAnd("Enumeration", UtilMisc.toMap("enumId", partySupplementalData.getString("leadStatus")), null, false) );
								if (UtilValidate.isNotEmpty(enumeration)) {
									leadStatus = enumeration.getString("description");
								}
							}*/
							
							SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
							callBackDate = partySupplementalData.getString("lastCallBackDate");
							lastContactDate = UtilValidate.isNotEmpty(partySupplementalData.getString("lastContactDate")) ? partySupplementalData.getString("lastContactDate") : "";
							if (UtilValidate.isNotEmpty(lastContactDate)) {
		                        String currentDate = formatter.format(new Date());
		                        Date d1 = new SimpleDateFormat("yyyy-MM-dd").parse(lastContactDate);
		                        Date d2 = formatter.parse(currentDate);
		                        long diff = d2.getTime() - d1.getTime();
		                        long diffDay = diff / (24 * 60 * 60 * 1000);
		                        diffDays = String.valueOf(diffDay);
		                    }
							if(UtilValidate.isNotEmpty(callBackDate)) {
							  Date callBackDate1 = new SimpleDateFormat("yyyy-MM-dd").parse(callBackDate);
	                          callBackDate = sdf.format(callBackDate1);
							}
							
							partyDetails.put("createdStamp", UtilValidate.isNotEmpty(partySupplementalData.get("createdStamp")) ? UtilDateTime.timeStampToString(partySupplementalData.getTimestamp("createdStamp"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
							
							if (UtilValidate.isNotEmpty(partySupplementalData.getString("leadScore"))) {
								GenericValue enumeration = EntityUtil.getFirst( delegator.findByAnd("Enumeration", UtilMisc.toMap("enumId", partySupplementalData.getString("leadScore")), null, false) );
								if (UtilValidate.isNotEmpty(enumeration)) {
									leadClassification = enumeration.getString("description");
								}
							}
						}
						
						GenericValue leadStatusList = EntityUtil.getFirst( delegator.findByAnd("LeadStatus", UtilMisc.toMap("leadId", leadId), null, false) );
						if (UtilValidate.isNotEmpty(leadStatusList)) {
							if (UtilValidate.isNotEmpty(leadStatusList.getString("statusId"))) {
								GenericValue enumeration = EntityUtil.getFirst( delegator.findByAnd("Enumeration", UtilMisc.toMap("enumCode", leadStatusList.getString("statusId")), null, false) );
								if (UtilValidate.isNotEmpty(enumeration)) {
									leadStatus = enumeration.getString("description");
								}
							}
						}
						
						String leadSubStatus = "";
						String leadSubStatusId = "";
						GenericValue leadSubStatusList = EntityUtil.getFirst( delegator.findByAnd("WorkEffortLog", UtilMisc.toMap("companyId", leadId), UtilMisc.toList("lastUpdatedStamp DESC"), false) );
						if (UtilValidate.isNotEmpty(leadSubStatusList)) {
							if (UtilValidate.isNotEmpty(leadSubStatusList.getString("outcomeId"))) {
								GenericValue enumeration = EntityUtil.getFirst( delegator.findByAnd("Enumeration", UtilMisc.toMap("enumId", leadSubStatusList.getString("outcomeId")), null, false) );
								if (UtilValidate.isNotEmpty(enumeration)) {
									leadSubStatusId = enumeration.getString("enumId");
									leadSubStatus = enumeration.getString("description");
								}
							}
						}
						if (UtilValidate.isNotEmpty(leadSubStatusInp)) {
							if (UtilValidate.isNotEmpty(leadSubStatusId)) {
								List<String> leadSubStatusInpList = UtilCommon.getArrayToList(leadSubStatusInp);
								if(!leadSubStatusInpList.contains(leadSubStatusId)) {
									continue;
								}
							}
							else {
								continue;
							}
						}
						
						String leadAssignFromName = "";
						if (UtilValidate.isNotEmpty(partySupplementalData.getString("leadAssignBy"))) {
							leadAssignFromName = PartyHelper.getPartyName(delegator, partySupplementalData.getString("leadAssignBy"), false);
						}
						//Displaying lead assigned from id , if leadAssignBy is empty
						if(UtilValidate.isEmpty(leadAssignFromName)) {
							GenericValue isOneBankIdExists = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", partySupplementalData.getString("uploadedByUserLoginId")), true);
							leadAssignFromName = PartyHelper.getPartyName(delegator, isOneBankIdExists.getString("partyId"), false);
						}
						String leadAssignToName = "";
						String managerName = "";
						String managerId = "";
						if (UtilValidate.isNotEmpty(partySupplementalData.getString("leadAssignTo"))) {
							if(!"admin".equalsIgnoreCase(userLogin.getString("userLoginId")) && isTeamLead) {
								List<GenericValue> aoRoles = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", userLogin.getString("partyId"), "roleTypeId", "DBS_CENTRAL"), null, false);
								if(UtilValidate.isEmpty(aoRoles)) {
									if(!multiTeamLists.contains(partySupplementalData.getString("leadAssignTo"))) {
										continue;
									}
								}
							}
							leadAssignToName = PartyHelper.getPartyName(delegator, partySupplementalData.getString("leadAssignTo"), false);
							GenericValue partyIdFrom = EntityUtil.getFirst(delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", partySupplementalData.getString("leadAssignTo"), "roleTypeIdFrom", "ACCOUNT_TEAM"), UtilMisc.toList("fromDate DESC"), false));
							if(UtilValidate.isNotEmpty(partyIdFrom)) {
								Map<String, Object> vTeamMemberList = VirtualTeamUtil.getVirtualTeamLead(delegator, partyIdFrom.getString("partyIdFrom"));
								if(UtilValidate.isNotEmpty(vTeamMemberList)) {	
									managerId = (String)vTeamMemberList.get("virtualTeamMemberId");
									managerName = (String)vTeamMemberList.get("virtualTeamMemberName");
								}
							}
						}
						if(UtilValidate.isNotEmpty(userManager)) {
							if(UtilValidate.isNotEmpty(managerId)) {
								List<String> userManagerList = UtilCommon.getArrayToList(userManager);
								if(!userManagerList.contains(managerId)) {
									continue;
								}
							}
							else {
								continue;
							}
						}
													
						String lastCalledDate = null;
						GenericValue workEffortCall = EntityUtil.getFirst( delegator.findByAnd("WorkEffort", UtilMisc.toMap("workEffortName", partySummaryDetailsView.getString("companyName"), "workEffortTypeId", "LMS_CALL"), UtilMisc.toList("estimatedCompletionDate DESC"), false) );
						if (UtilValidate.isNotEmpty(workEffortCall)) {
							lastCalledDate = workEffortCall.getString("estimatedCompletionDate");
						}
						
						String lastMeetingDate = "";
						GenericValue workEffortMeeting = EntityUtil.getFirst( delegator.findByAnd("WorkEffort", UtilMisc.toMap("workEffortName", partySummaryDetailsView.getString("companyName"), "workEffortTypeId", "LMS_MEETING"), UtilMisc.toList("estimatedCompletionDate DESC"), false) );
						if (UtilValidate.isNotEmpty(workEffortMeeting)) {
							lastMeetingDate = workEffortMeeting.getString("estimatedCompletionDate");
						}
						
						String lastCallLogUpdatedDate = "";
						EntityCondition conditionCallLogUpdate = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("companyId", EntityOperator.EQUALS, leadId),
								EntityCondition.makeCondition("outcomeId", EntityOperator.IN, UtilMisc.toList("APPOINTMENT_FIXED","CUST_INTRST_OPN_ACCT","DROPPED","ENGAGED", "REQUIRES_CALLBACK"))), EntityOperator.AND);
	
						GenericValue workEffortLogCallUpdate = EntityQuery.use(delegator).from("WorkEffortLog").where(conditionCallLogUpdate).orderBy("lastUpdatedStamp DESC").queryFirst();
						if (UtilValidate.isNotEmpty(workEffortLogCallUpdate)) {
							lastCallLogUpdatedDate = workEffortLogCallUpdate.getString("lastUpdatedStamp");
						}
						
						String lastMeetingLogUpdateDate = "";
						EntityCondition conditionMeetingLogUpdate = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("companyId", EntityOperator.EQUALS, leadId),
								EntityCondition.makeCondition("outcomeId", EntityOperator.IN, UtilMisc.toList("CUSTOMER_INTRESTED","DIDNOT_HAPPEN","DROPPED_LEAD","OPEN_ACCOUNT"))), EntityOperator.AND);
	
						GenericValue workEffortLogMeetingUpdate = EntityQuery.use(delegator).from("WorkEffortLog").where(conditionMeetingLogUpdate).orderBy("lastUpdatedStamp DESC").queryFirst();
						if (UtilValidate.isNotEmpty(workEffortLogMeetingUpdate)) {
							lastMeetingLogUpdateDate = workEffortLogMeetingUpdate.getString("lastUpdatedStamp");
						}
	
						List<EntityCondition> attemptConditions = new ArrayList <EntityCondition>();			
						attemptConditions.add( EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("leadId", EntityOperator.EQUALS, leadId),
								EntityCondition.makeCondition("type", EntityOperator.EQUALS, "LMS_CALL")));
						
						long callAttempts = delegator.findCountByCondition("LeadCallHistory", EntityCondition.makeCondition(attemptConditions), null, null);
						
						List<EntityCondition> conditionsMeet = new ArrayList <EntityCondition>();			
						conditionsMeet.add( EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, leadId),
								EntityCondition.makeCondition("workEffortTypeId", EntityOperator.EQUALS, "LMS_MEETING"),
								EntityCondition.makeCondition("estimatedCompletionDate", EntityOperator.LESS_THAN, UtilDateTime.nowTimestamp()),
								EntityCondition.makeCondition("currentStatusId", EntityOperator.NOT_EQUAL, "EVENT_CANCELLED")
								));
						
						long meetingCount = delegator.findCountByCondition("WorkEffortAndPartyAssign", EntityCondition.makeCondition(conditionsMeet), null, null);
						
						callAttempts = meetingCount + callAttempts;
						// Person Responsible for  [end]
	
						partyDetails.put("partyId", leadId);
						partyDetails.put("name", name);
						partyDetails.put("firstName", UtilValidate.isNotEmpty(partySummaryDetailsView.getString("firstName")) ? partySummaryDetailsView.getString("firstName") : partySummaryDetailsView.getString("partyFirstName"));
						partyDetails.put("callBackDate", callBackDate);
						partyDetails.put("companyName", companyNameDetails);
						partyDetails.put("statusDescription", statusItemDesc);
						partyDetails.put("dataSourceDesc", dataSourceDesc);
						partyDetails.put("contactNumber", phoneNumber);
						partyDetails.put("emailAddress", infoString);
						partyDetails.put("city", city);
						partyDetails.put("state", state);
						partyDetails.put("cityName", cityName);
	
						partyDetails.put("personResponsible", personResponsible);
						partyDetails.put("personResponsibleAssignBy", personResponsibleAssignBy);
						partyDetails.put("segment", segment);
						partyDetails.put("liabOrAsset", liabOrAsset);
						partyDetails.put("teleCallingStatus", teleCallingStatus);
						partyDetails.put("leadStatus", leadStatus);
						partyDetails.put("diffDays", diffDays);
						partyDetails.put("noOfDateSinceLastCall", lastContactDate);
	
						partyDetails.put("leadSubStatus", leadSubStatus);
						partyDetails.put("leadAssignTo", UtilValidate.isNotEmpty(partySummaryDetailsView.getString("leadAssignTo")));
						partyDetails.put("leadAssignFromName", leadAssignFromName);
						partyDetails.put("leadAssignToName", leadAssignToName);
						partyDetails.put("noOfAttempt", callAttempts); //partySummaryDetailsView.getString("noOfAttempt")
						partyDetails.put("lastCalledDate", lastCalledDate);
						partyDetails.put("lastMeetingDate", lastMeetingDate);
						partyDetails.put("lastCallLogUpdatedDate", lastCallLogUpdatedDate);
						partyDetails.put("lastMeetingLogUpdateDate", lastMeetingLogUpdateDate);
						partyDetails.put("leadClassification", leadClassification);
						partyDetails.put("daysInQueue", daysInQueue);
						partyDetails.put("createdTxStamp", createdTxStamp);
						partyDetails.put("managerName", managerName);
						results.add(partyDetails);
					}
				}
				//}	
				Debug.log("Results : " + results, MODULE);
			} catch (Exception e) {
				//e.printStackTrace();
	    		Debug.logError(e.getMessage(), MODULE);

	            Map<String, Object> data = new HashMap<String, Object>();
	            data.put("errorMessage", e.getMessage());
	            data.put("errorResult", new ArrayList<Map<String, Object>>());
	            results.add(data);
			}
			return AjaxEvents.doJSONResponse(response, results);
	}

	/*
	 * Get Account Details 
	 */
	public static String getAccountDetails(HttpServletRequest request, HttpServletResponse response) {
		
		// old view: PartyFromByRelnAndContactInfoAndPartySupplemantalData
		// new view: AccountSummaryView

		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");		
		
		String accountSearchPartyId = request.getParameter("accountSearchPartyId");
		String searchGroupName = request.getParameter("searchGroupName");
		String searchEmailId = request.getParameter("searchEmailId");
		String searchPhoneNum = request.getParameter("searchPhoneNum");
		
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        Map<String, Object> context = UtilHttp.getCombinedMap(request);
		try {			
			List < EntityCondition > conditions = new ArrayList < EntityCondition > ();

			// construct role conditions
			EntityCondition roleTypeCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ACCOUNT"));
			conditions.add(roleTypeCondition);

			EntityCondition partyStatusCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
					EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)), EntityOperator.OR);

			conditions.add(partyStatusCondition);
			conditions.add(EntityUtil.getFilterByDateExpr());

			if (UtilValidate.isNotEmpty(accountSearchPartyId)) {
				EntityCondition partyCondition = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, accountSearchPartyId);
				conditions.add(partyCondition);
			}

			if (UtilValidate.isNotEmpty(searchGroupName)) {
				EntityCondition nameCondition = EntityCondition.makeCondition("groupName", EntityOperator.LIKE, "%" + searchGroupName + "%");
				conditions.add(nameCondition);
			}

			List < EntityCondition > eventExprs = new LinkedList < EntityCondition > ();
			if (UtilValidate.isNotEmpty(searchEmailId) || UtilValidate.isNotEmpty(searchPhoneNum)) {

				if (UtilValidate.isNotEmpty(searchEmailId)) {
					EntityCondition emailCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("infoString", EntityOperator.LIKE, searchEmailId + "%"),
							EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "EMAIL_ADDRESS")), EntityOperator.AND);
					eventExprs.add(emailCondition);
				}

				if (UtilValidate.isNotEmpty(searchPhoneNum)) {
					EntityCondition phoneCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("contactNumber", EntityOperator.EQUALS, searchPhoneNum),
							EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "TELECOM_NUMBER")), EntityOperator.AND);
					eventExprs.add(phoneCondition);
				}

				conditions.add(EntityCondition.makeCondition(eventExprs, EntityOperator.OR));
			}
			//Login Based account Filter
            String userLoginId = userLogin.getString("partyId");
            if(LoginFilterUtil.checkEmployeePosition(delegator, userLoginId)){
				Map<String, Object> dataSecurityMetaInfo = (Map<String, Object>) session.getAttribute("dataSecurityMetaInfo");
				if (ResponseUtils.isSuccess(dataSecurityMetaInfo)) {
	
					List<String> lowerPositionPartyIds = (List<String>) dataSecurityMetaInfo.get("lowerPositionPartyIds");
					if (UtilValidate.isNotEmpty(lowerPositionPartyIds)) {
	
						EntityCondition securityConditions = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, lowerPositionPartyIds),
								EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
								EntityUtil.getFilterByDateExpr()
								);
						
						if (UtilValidate.isNotEmpty(userLogin)) {
							securityConditions = EntityCondition.makeCondition(UtilMisc.toList(
									EntityCondition.makeCondition("uploadedByUserLoginId", EntityOperator.EQUALS, userLogin.getString("userLoginId")),
									securityConditions
								), EntityOperator.OR);
						}
	
						conditions.add(securityConditions);
					}
	
					Debug.log("lowerPositionPartyIds> "+lowerPositionPartyIds);
	
				}
            }
			EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			EntityFindOptions efo = new EntityFindOptions();
			efo.setDistinct(true);
			efo.getDistinct();
			
			List < GenericValue > parties=null;
			Debug.logInfo("list 1 start: "+UtilDateTime.nowTimestamp(), MODULE);
			parties = delegator.findList("AccountSummaryView", mainConditons, UtilMisc.toSet("partyId"), null, efo, false);
			Debug.logInfo("list 2 start: "+UtilDateTime.nowTimestamp(), MODULE);
			
			for(GenericValue partyIdList: parties){
				String partyId= partyIdList.getString("partyId"); 
				GenericValue partySummaryDetailsViewGv = delegator.findOne("PartySummaryDetailsView", UtilMisc.toMap("partyId", partyId), false);
				if (partySummaryDetailsViewGv != null && partySummaryDetailsViewGv.size() > 0) {
					Map < String, Object > partyDetails = new HashMap < String, Object > ();
					String groupName = partySummaryDetailsViewGv.getString("groupName");
					String statusId = partySummaryDetailsViewGv.getString("statusId");
					String statusItemDesc = "";

					if (UtilValidate.isNotEmpty(statusId)) {
						GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
						if (statusItem != null && statusItem.size() > 0) {
							statusItemDesc = statusItem.getString("description");
						}
					}
					String dataSourceDesc = "";
					GenericValue partyDataSource = EntityUtil.getFirst(delegator.findByAnd("PartyDataSource", UtilMisc.toMap("partyId", partyId), UtilMisc.toList("fromDate DESC"), false));
					if (partyDataSource != null && partyDataSource.size() > 0) {
						String dataSourceId = partyDataSource.getString("dataSourceId");
						if (UtilValidate.isNotEmpty(dataSourceId)) {
							GenericValue dataSource = delegator.findOne("DataSource", UtilMisc.toMap("dataSourceId", dataSourceId), false);
							if (dataSource != null && dataSource.size() > 0) {
								dataSourceDesc = dataSource.getString("description");
							}
						}
					}
					String phoneNumber = "";
					String infoString = "";
					String city = "";
					String state = "";
					String lcin = "";
					String cin = "";
					List < GenericValue > partyContactMechs = delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", partyId, "allowSolicitation", "Y"), null, false);
					if (partyContactMechs != null && partyContactMechs.size() > 0) {
						partyContactMechs = EntityUtil.filterByDate(partyContactMechs);
						if (partyContactMechs != null && partyContactMechs.size() > 0) {
							partyContactMechs = EntityUtil.getFieldListFromEntityList(partyContactMechs, "contactMechId", true);
						}
						if (partyContactMechs != null && partyContactMechs.size() > 0) {
							Set < String > findOptions = UtilMisc.toSet("contactMechId");
							List < String > orderBy = UtilMisc.toList("createdStamp DESC");

							EntityCondition condition1 = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
							EntityCondition condition2 = EntityCondition.makeCondition("contactMechId", EntityOperator.IN, partyContactMechs);

							EntityCondition primaryPhoneConditions = EntityCondition.makeCondition(UtilMisc.toList(condition1, condition2, EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_PHONE")));
							List < GenericValue > primaryPhones = delegator.findList("PartyContactMechPurpose", primaryPhoneConditions, findOptions, null, null, false);
							if (primaryPhones != null && primaryPhones.size() > 0) {
								GenericValue primaryPhone = EntityUtil.getFirst(EntityUtil.filterByDate(primaryPhones));
								if (UtilValidate.isNotEmpty(primaryPhone)) {
									GenericValue primaryPhoneNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", primaryPhone.getString("contactMechId")), false);
									if (UtilValidate.isNotEmpty(primaryPhoneNumber)) {
										phoneNumber = primaryPhoneNumber.getString("contactNumber");
									}
								}
							}

							EntityCondition primaryEmailConditions = EntityCondition.makeCondition(UtilMisc.toList(condition1, condition2, EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_EMAIL")));
							List < GenericValue > primaryEmails = delegator.findList("PartyContactMechPurpose", primaryEmailConditions, findOptions, null, null, false);
							if (primaryEmails != null && primaryEmails.size() > 0) {
								GenericValue primaryEmail = EntityUtil.getFirst(EntityUtil.filterByDate(primaryEmails));
								if (UtilValidate.isNotEmpty(primaryEmail)) {
									GenericValue primaryInfoString = delegator.findOne("ContactMech", UtilMisc.toMap("contactMechId", primaryEmail.getString("contactMechId")), false);
									if (UtilValidate.isNotEmpty(primaryInfoString)) {
										infoString = primaryInfoString.getString("infoString");
									}
								}
							}

							EntityCondition postalAddressConditions = EntityCondition.makeCondition(UtilMisc.toList(condition1, condition2, EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_LOCATION")));
							List < GenericValue > primaryAddressList = delegator.findList("PartyContactMechPurpose", postalAddressConditions, findOptions, null, null, false);
							if (primaryAddressList != null && primaryAddressList.size() > 0) {
								GenericValue primaryAddress = EntityUtil.getFirst(EntityUtil.filterByDate(primaryAddressList));
								if (UtilValidate.isNotEmpty(primaryAddress)) {
									GenericValue postalAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", primaryAddress.getString("contactMechId")), false);
									if (UtilValidate.isNotEmpty(postalAddress)) {
										city = postalAddress.getString("city");
										String stateProvinceGeoId = postalAddress.getString("stateProvinceGeoId");
										if (UtilValidate.isNotEmpty(stateProvinceGeoId)) {
											GenericValue geo = delegator.findOne("Geo", UtilMisc.toMap("geoId", stateProvinceGeoId), false);
											if (UtilValidate.isNotEmpty(geo)) {
												state = geo.getString("geoName");
											}
										}
									}
								}
							}
						}
					}
					partyDetails.put("partyId", partyId);
					partyDetails.put("groupName", groupName);
					partyDetails.put("statusDescription", statusItemDesc);
					partyDetails.put("dataSourceDesc", dataSourceDesc);
					partyDetails.put("contactNumber", phoneNumber);
					partyDetails.put("infoString", infoString);
					partyDetails.put("city", city);
					partyDetails.put("state", state);
					partyDetails.put("lcin", lcin);
					partyDetails.put("cin", cin);
					results.add(partyDetails);
				}
			}
			Debug.log("Results : " + results, MODULE);
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("errorMessage", e.getMessage());
            data.put("errorResult", new ArrayList<Map<String, Object>>());
            results.add(data);
		}
		return AjaxEvents.doJSONResponse(response, results);
	}
	/*
	 * Get Contact Details 
	 */
	public static String getContactDetails(HttpServletRequest request, HttpServletResponse response) {		
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        Map<String, Object> context = UtilHttp.getCombinedMap(request);
		
		String partyId = request.getParameter("partyId");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String emailAddress = request.getParameter("emailAddress");
		String contactNumber = request.getParameter("contactNumber");
		try {
			List < EntityCondition > conditions = new ArrayList < EntityCondition > ();

			// construct role conditions
			EntityCondition roleTypeCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"));
			conditions.add(roleTypeCondition);

			EntityCondition partyStatusCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
					EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)), EntityOperator.OR);

			conditions.add(partyStatusCondition);
			conditions.add(EntityUtil.getFilterByDateExpr());

			if (UtilValidate.isNotEmpty(partyId)) {
				EntityCondition partyCondition = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
				conditions.add(partyCondition);
			}

			if (UtilValidate.isNotEmpty(firstName)) {
				EntityCondition firstNameCondition = EntityCondition.makeCondition("firstName", EntityOperator.LIKE, "%"+firstName + "%");
				conditions.add(firstNameCondition);
			}
			if (UtilValidate.isNotEmpty(lastName)) {
				EntityCondition lastNameCondition = EntityCondition.makeCondition("lastName", EntityOperator.LIKE, "%"+lastName + "%");
				conditions.add(lastNameCondition);
			}

			List < EntityCondition > eventExprs = new LinkedList < EntityCondition > ();
			if (UtilValidate.isNotEmpty(emailAddress) || UtilValidate.isNotEmpty(contactNumber)) {

				if (UtilValidate.isNotEmpty(emailAddress)) {
					EntityCondition emailCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("infoString", EntityOperator.EQUALS, emailAddress),
							EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "EMAIL_ADDRESS")), EntityOperator.AND);
					eventExprs.add(emailCondition);
				}

				if (UtilValidate.isNotEmpty(contactNumber)) {
					EntityCondition phoneCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("contactNumber", EntityOperator.EQUALS, contactNumber),
							EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "TELECOM_NUMBER")), EntityOperator.AND);
					eventExprs.add(phoneCondition);
				}

				conditions.add(EntityCondition.makeCondition(eventExprs, EntityOperator.OR));
			}
						
			//Login Based contact Filter
            String userLoginId = userLogin.getString("partyId");
            if(LoginFilterUtil.checkEmployeePosition(delegator, userLoginId)) {
            	
				Map<String, Object> dataSecurityMetaInfo = (Map<String, Object>) session.getAttribute("dataSecurityMetaInfo");
	            if (ResponseUtils.isSuccess(dataSecurityMetaInfo)) {
	
	            	List<String> lowerPositionPartyIds = (List<String>) dataSecurityMetaInfo.get("lowerPositionPartyIds");
	            	if (UtilValidate.isNotEmpty(lowerPositionPartyIds)) {
	
	            		List<EntityCondition> accountConditions = new ArrayList<EntityCondition>();
	            		EntityCondition accountRoleTypeCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, UtilMisc.toList("ACCOUNT", "LEAD")));
	            		accountConditions.add(accountRoleTypeCondition);

	        			EntityCondition accountPartyStatusCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
	        			EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)), EntityOperator.OR);

	        			accountConditions.add(accountPartyStatusCondition);
	        			accountConditions.add(EntityUtil.getFilterByDateExpr());
	            		
	            		EntityCondition securityConditions = EntityCondition.makeCondition(EntityOperator.AND,
	    						EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, lowerPositionPartyIds),
	    						EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
	    						EntityUtil.getFilterByDateExpr()
	    						);
	            		
	            		if (UtilValidate.isNotEmpty(userLogin)) {
							securityConditions = EntityCondition.makeCondition(UtilMisc.toList(
									EntityCondition.makeCondition("uploadedByUserLoginId", EntityOperator.EQUALS, userLogin.getString("userLoginId")),
									securityConditions
								), EntityOperator.OR);
						}
	            		
	            		accountConditions.add(securityConditions);
	            		
	            		EntityCondition mainConditons = EntityCondition.makeCondition(accountConditions, EntityOperator.AND);
	            		
	            		EntityFindOptions efo = new EntityFindOptions();
	        			efo.setDistinct(true);
	        			efo.getDistinct();
	        			
	        			Debug.logInfo("count 1 start: "+UtilDateTime.nowTimestamp(), MODULE);
	        			List<GenericValue> accounts = delegator.findList("PartyCommonView", mainConditons, UtilMisc.toSet("partyId"), UtilMisc.toList("partyId"+ " " + "ASC"), efo, false);
	        			Debug.logInfo("count 2 start: "+UtilDateTime.nowTimestamp(), MODULE);
	        			
	        			List<String> accountPartyIds = EntityUtil.getFieldListFromEntityList(accounts, "partyId", true);
	        			
	        			EntityCondition partyIdToCondition = EntityCondition.makeCondition(UtilMisc.toList(
	                            EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, accountPartyIds),
	                            EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, accountPartyIds)
	                        ),EntityOperator.OR);
	                    conditions.add(partyIdToCondition);	            		            	
	            	}
	
	            	Debug.log("lowerPositionPartyIds> "+lowerPositionPartyIds);
	
	            }
            }
            
			EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			
			EntityFindOptions efo = new EntityFindOptions();
			
			efo.setDistinct(true);
			efo.getDistinct();					
			
			Debug.logInfo("list 1 start: "+UtilDateTime.nowTimestamp(), MODULE);
			List < GenericValue > parties = delegator.findList("ContactSummaryView", mainConditons, UtilMisc.toSet("partyId"), null, efo, false);
			Debug.logInfo("list 2 start: "+UtilDateTime.nowTimestamp(), MODULE);
						
			for(GenericValue partyIdList: parties){
				String contactId= partyIdList.getString("partyId"); 
				GenericValue partySummaryCRMView = delegator.findOne("PartySummaryDetailsView", UtilMisc.toMap("partyId", contactId), false);
				if (partySummaryCRMView != null && partySummaryCRMView.size() > 0) {
					Map < String, Object > partyDetails = new HashMap < String, Object > ();
					String callBackDate = partySummaryCRMView.getString("callBackDate");
					String companyName = partySummaryCRMView.getString("companyName");
					String companyId = "";
					String statusId = partySummaryCRMView.getString("statusId");
					String generalProfTitle = partySummaryCRMView.getString("generalProfTitle");
					String statusItemDesc = "";
					String name = partySummaryCRMView.getString("firstName");
					if (UtilValidate.isNotEmpty(partySummaryCRMView.getString("lastName"))) {
						name = name + " " + partySummaryCRMView.getString("lastName");
					}
					if (UtilValidate.isNotEmpty(statusId)) {
						GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
						if (statusItem != null && statusItem.size() > 0) {
							statusItemDesc = statusItem.getString("description");
						}
					}
					String dataSourceDesc = "";
					GenericValue partyDataSource = EntityUtil.getFirst(delegator.findByAnd("PartyDataSource", UtilMisc.toMap("partyId", contactId), UtilMisc.toList("fromDate DESC"), false));
					if (partyDataSource != null && partyDataSource.size() > 0) {
						String dataSourceId = partyDataSource.getString("dataSourceId");
						if (UtilValidate.isNotEmpty(dataSourceId)) {
							GenericValue dataSource = delegator.findOne("DataSource", UtilMisc.toMap("dataSourceId", dataSourceId), false);
							if (dataSource != null && dataSource.size() > 0) {
								dataSourceDesc = dataSource.getString("description");
							}
						}
					}
					String phoneNumber = "";
					String infoString = "";
					String city = "";
					String state = "";
					String lcin = "";
					String cin = "";
					List < GenericValue > partyContactMechs = delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", contactId, "allowSolicitation", "Y"), null, false);
					if (partyContactMechs != null && partyContactMechs.size() > 0) {
						partyContactMechs = EntityUtil.filterByDate(partyContactMechs);
						if (partyContactMechs != null && partyContactMechs.size() > 0) {
							partyContactMechs = EntityUtil.getFieldListFromEntityList(partyContactMechs, "contactMechId", true);
						}
						if (partyContactMechs != null && partyContactMechs.size() > 0) {
							Set < String > findOptions = UtilMisc.toSet("contactMechId");
							List < String > orderBy = UtilMisc.toList("createdStamp DESC");

							EntityCondition condition1 = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, contactId);
							EntityCondition condition2 = EntityCondition.makeCondition("contactMechId", EntityOperator.IN, partyContactMechs);

							EntityCondition primaryPhoneConditions = EntityCondition.makeCondition(UtilMisc.toList(condition1, condition2, EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_PHONE")));
							List < GenericValue > primaryPhones = delegator.findList("PartyContactMechPurpose", primaryPhoneConditions, findOptions, null, null, false);
							if (primaryPhones != null && primaryPhones.size() > 0) {
								GenericValue primaryPhone = EntityUtil.getFirst(EntityUtil.filterByDate(primaryPhones));
								if (UtilValidate.isNotEmpty(primaryPhone)) {
									GenericValue primaryPhoneNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", primaryPhone.getString("contactMechId")), false);
									if (UtilValidate.isNotEmpty(primaryPhoneNumber)) {
										phoneNumber = primaryPhoneNumber.getString("contactNumber");
									}
								}
							}

							EntityCondition primaryEmailConditions = EntityCondition.makeCondition(UtilMisc.toList(condition1, condition2, EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_EMAIL")));
							List < GenericValue > primaryEmails = delegator.findList("PartyContactMechPurpose", primaryEmailConditions, findOptions, null, null, false);
							if (primaryEmails != null && primaryEmails.size() > 0) {
								GenericValue primaryEmail = EntityUtil.getFirst(EntityUtil.filterByDate(primaryEmails));
								if (UtilValidate.isNotEmpty(primaryEmail)) {
									GenericValue primaryInfoString = delegator.findOne("ContactMech", UtilMisc.toMap("contactMechId", primaryEmail.getString("contactMechId")), false);
									if (UtilValidate.isNotEmpty(primaryInfoString)) {
										infoString = primaryInfoString.getString("infoString");
									}
								}
							}

							EntityCondition postalAddressConditions = EntityCondition.makeCondition(UtilMisc.toList(condition1, condition2, EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_LOCATION")));
							List < GenericValue > primaryAddressList = delegator.findList("PartyContactMechPurpose", postalAddressConditions, findOptions, null, null, false);
							if (primaryAddressList != null && primaryAddressList.size() > 0) {
								GenericValue primaryAddress = EntityUtil.getFirst(EntityUtil.filterByDate(primaryAddressList));
								if (UtilValidate.isNotEmpty(primaryAddress)) {
									GenericValue postalAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", primaryAddress.getString("contactMechId")), false);
									if (UtilValidate.isNotEmpty(postalAddress)) {
										city = postalAddress.getString("city");
										String stateProvinceGeoId = postalAddress.getString("stateProvinceGeoId");
										if (UtilValidate.isNotEmpty(stateProvinceGeoId)) {
											GenericValue geo = delegator.findOne("Geo", UtilMisc.toMap("geoId", stateProvinceGeoId), false);
											if (UtilValidate.isNotEmpty(geo)) {
												state = geo.getString("geoName");
											}
										}
									}
								}
							}
						}
					}

					List < EntityCondition > contactConditions = new ArrayList < EntityCondition > ();
					contactConditions.add(EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV"),
							EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, UtilMisc.toList("ACCOUNT", "LEAD")),
							EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, contactId),
							EntityUtil.getFilterByDateExpr()));
					GenericValue partyRelationship = EntityQuery.use(delegator).from("PartyRelationship").where(contactConditions).orderBy("fromDate DESC").queryFirst();
					if(partyRelationship != null && partyRelationship.size() > 0) {
						companyId = partyRelationship.getString("partyIdTo");
						if(UtilValidate.isNotEmpty(companyId)) {
							GenericValue cmpanyName = EntityQuery.use(delegator).from("PartyGroup").where("partyId",companyId).queryOne();
							if(partyRelationship != null && partyRelationship.size() > 0) {
								companyName = cmpanyName.getString("groupName");
							}
						}
					}

					partyDetails.put("partyId", contactId);
					partyDetails.put("name", name);
					partyDetails.put("generalProfTitle", generalProfTitle);
					partyDetails.put("callBackDate", callBackDate);
					partyDetails.put("statusDescription", statusItemDesc);
					partyDetails.put("dataSourceDesc", dataSourceDesc);
					partyDetails.put("contactNumber", phoneNumber);
					partyDetails.put("infoString", infoString);
					partyDetails.put("city", city);
					partyDetails.put("state", state);
					partyDetails.put("groupName", companyName);
					partyDetails.put("partyIdTo", companyId);
					partyDetails.put("lcin", lcin);
					partyDetails.put("cin", cin);
					results.add(partyDetails);
				}
			}					
			Debug.log("Results : " + results, MODULE);
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("errorMessage", e.getMessage());
            data.put("errorResult", new ArrayList<Map<String, Object>>());
            results.add(data);
		}
		return AjaxEvents.doJSONResponse(response, results);
	}
	
	@SuppressWarnings("unchecked")
	public static String getIndustryList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		HttpSession session = request.getSession(true);

		String industryCatId = request.getParameter("industryCatId");

		Map<String, Object> resp = new HashMap<String, Object>();
		try {

			Map<String, Object> requestParams = UtilMisc.toMap();
			List<Map<String, Object>> industrys = new ArrayList<Map<String, Object>>();

			requestParams.put("enumTypeId", "DBS_INDUSTRY");

			if (UtilValidate.isNotEmpty(industryCatId)) {
				requestParams.put("parentEnumId", industryCatId);
			}

			List<GenericValue> industryList = delegator.findByAnd("Enumeration", requestParams, UtilMisc.toList("sequenceId"), false);
			for (GenericValue industry : industryList) {
				Map<String, Object> ser = new HashMap<String, Object>();
				ser.put("description", industry.getString("description"));
				ser.put("enumCode", industry.getString("enumCode"));
				ser.put("enumId", industry.getString("enumId"));
				ser.put("enumTypeId", industry.getString("enumTypeId"));
				ser.put("parentEnumId", industry.getString("parentEnumId"));
				ser.put("sequenceId", industry.getString("sequenceId"));

				industrys.add(ser);
			}

			resp.put("industrys", industrys);

			resp.put(CrmConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);

		} catch (Exception e) {
			//e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);

			resp.put(CrmConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			resp.put(CrmConstants.RESPONSE_MESSAGE, e.getMessage());

			return doJSONResponse(response, resp);
		}

		return doJSONResponse(response, resp);
	}
	
	@SuppressWarnings("unchecked")
	public static String getEnumList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		HttpSession session = request.getSession(true);

		String parentEnumId = request.getParameter("parentEnumId");
		String enumTypeId = request.getParameter("enumTypeId");

		Map<String, Object> resp = new HashMap<String, Object>();
		try {

			Map<String, Object> requestParams = UtilMisc.toMap();
			List<Map<String, Object>> enums = new ArrayList<Map<String, Object>>();

			requestParams.put("enumTypeId", enumTypeId);

			if (UtilValidate.isNotEmpty(parentEnumId)) {
				requestParams.put("parentEnumId", parentEnumId);
			}

			List<GenericValue> enumList = delegator.findByAnd("Enumeration", requestParams, UtilMisc.toList("sequenceId"), false);
			for (GenericValue enumObj : enumList) {
				Map<String, Object> ser = new HashMap<String, Object>();
				ser.put("description", enumObj.getString("description"));
				ser.put("enumCode", enumObj.getString("enumCode"));
				ser.put("enumId", enumObj.getString("enumId"));
				ser.put("enumTypeId", enumObj.getString("enumTypeId"));
				ser.put("parentEnumId", enumObj.getString("parentEnumId"));
				ser.put("sequenceId", enumObj.getString("sequenceId"));

				enums.add(ser);
			}

			resp.put("enums", enums);

			resp.put(CrmConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);

		} catch (Exception e) {
			//e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);

			resp.put(CrmConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			resp.put(CrmConstants.RESPONSE_MESSAGE, e.getMessage());

			return doJSONResponse(response, resp);
		}

		return doJSONResponse(response, resp);
	}
	
	@SuppressWarnings("unchecked")
	public static String enableParty(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);

		String partyId = request.getParameter("partyId");
		String statusId = "LEAD_ASSIGNED";
		Timestamp statusDate = UtilDateTime.nowTimestamp();
		
		String isExecuteImport = request.getParameter("isExecuteImport");

		Map<String, Object> resp = new HashMap<String, Object>();

		try {

			if (UtilValidate.isNotEmpty(partyId)) {
				
				GenericValue party = EntityQuery.use(delegator).from("Party").where("partyId", partyId).queryOne();
				
				if (UtilValidate.isNotEmpty(party)) {
					
					party.set("statusId", "LEAD_ASSIGNED");
					party.store();
					
					GenericValue partyStatus = delegator.makeValue("PartyStatus", UtilMisc.toMap("partyId", partyId, "statusId", statusId, "statusDate", statusDate));
	                partyStatus.create();
	                
	                List <GenericValue> userLogins = EntityQuery.use(delegator).from("UserLogin").where("partyId", partyId).queryList();
                    for (GenericValue login : userLogins) {
                        if (!"N".equals(login.getString("enabled"))) {
                        	login.set("enabled", "N");
                        	login.store();
                        }
                    }
                    
                    if (UtilValidate.isNotEmpty(isExecuteImport) && isExecuteImport.equals("Y")) {
                    	GenericValue lead = EntityUtil.getFirst( delegator.findByAnd("DataImportLead", UtilMisc.toMap("primaryPartyId", partyId), null, false) );
                    	if (UtilValidate.isNotEmpty(lead)) {
                    		Map<String, Object> reqContext = FastMap.newInstance();
                			
                			reqContext.put("data", lead.getAllFields());
                			reqContext.put("userLogin", userLogin);
                			
                			reqContext.put("batchId", lead.getString("batchId"));
                			reqContext.put("taskName", "LEAD");
                			reqContext.put("tableName", "DataImportLead");
                			
                			Map<String, Object> validationResult = dispatcher.runSync("validator.validateLeadData", reqContext);
                			if (!ServiceUtil.isError(validationResult)) {
                				lead.put("leadId", validationResult.get("leadId"));
                				lead.put("errorCodes", validationResult.get("errorCodes"));
                				
                				TransactionUtil.begin();
                				
                				if (UtilValidate.isEmpty(validationResult.get("errorCodes"))) {
                					lead.put("importStatusId", "DATAIMP_APPROVED");
                				} else {
                					lead.put("importStatusId", "DATAIMP_ERROR");
                				}
                				
                				lead.store();
                				
                				TransactionUtil.commit();
                				
                				if (UtilValidate.isEmpty(validationResult.get("errorCodes"))) {
                					List<GenericValue> importDatas = new ArrayList<GenericValue>();
                					importDatas.add(lead);
                					
                					Map<String, Object> inputNew = new HashMap<String, Object>();
                					inputNew.put("organizationPartyId", "Company");
                					inputNew.put("userLogin", userLogin);
                					inputNew.put("importDatas", importDatas);
                					
                					Map<String, Object> result = dispatcher.runSync("importLeads", inputNew);
                					if (ServiceUtil.isSuccess(result)) {
                						Debug.logInfo("Successfully import lead: "+partyId, MODULE);
                					}
                				}
                			}
                    	}
                    }
					
					resp.put(CrmConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		            resp.put(CrmConstants.RESPONSE_MESSAGE, "Successfully enabled lead..");
					
				} else {
					resp.put(CrmConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
		            resp.put(CrmConstants.RESPONSE_MESSAGE, "Not found imported lead..");
				}
				
			}
			
		} catch (Exception e) {
			
			resp.put(CrmConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(CrmConstants.RESPONSE_MESSAGE, "Error: "+e.getMessage());
			
			Debug.logError(e.getMessage(), MODULE);
		}
		
		return doJSONResponse(response, resp);
	}

    @SuppressWarnings("unchecked")
	public static String getResponsibleForPartyList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);

		String jobFamily = request.getParameter("jobFamily");
		String leadScore = request.getParameter("leadScore");
		String virtualTeamId = request.getParameter("virtualTeamId");
		
		String countryGeoId = request.getParameter("countryGeoId");
		String city = request.getParameter("city");
		String postalCode = request.getParameter("postalCode");
		
		Timestamp statusDate = UtilDateTime.nowTimestamp();

		Map<String, Object> resp = new HashMap<String, Object>();

		try {
			
			//if ( (UtilValidate.isNotEmpty(jobFamily) || UtilValidate.isNotEmpty(leadScore) || UtilValidate.isNotEmpty(virtualTeamId)) && UtilValidate.isNotEmpty(city) && UtilValidate.isNotEmpty(countryGeoId)) {
			if (UtilValidate.isNotEmpty(city) && UtilValidate.isNotEmpty(countryGeoId)) {	
				
				List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
				
				String emplPositionTypeId = null;
				if (UtilValidate.isNotEmpty(leadScore) && leadScore.equals("LEAD_SCORE_HOT")) {
					emplPositionTypeId = "DBS_CENTRAL";
				} else if (UtilValidate.isNotEmpty(jobFamily) && jobFamily.equals("JOBFAMILY_0001")) {
					emplPositionTypeId = "DBS_TC";
				} else if (UtilValidate.isNotEmpty(jobFamily) && jobFamily.equals("JOBFAMILY_0002")) {
					emplPositionTypeId = "DBS_RM";
				}
				
				List<EntityCondition> conditions = new ArrayList <EntityCondition>();
				
				conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("emplPositionTypeId", EntityOperator.EQUALS, emplPositionTypeId),
						EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "EMPL_POS_OCCUPIED"),
						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"),
						EntityCondition.makeCondition("countryGeoId", EntityOperator.EQUALS, countryGeoId),
						EntityCondition.makeCondition("city", EntityOperator.EQUALS, city),
						EntityUtil.getFilterByDateExpr("actualFromDate", "actualThruDate")
						)
						);
				
				conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
						EntityUtil.getFilterByDateExpr()
						)
						);
				
				String userLoginPartyId = userLogin.getString("partyId");
				boolean isUserLoginAlreadyListed = false;
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				List<GenericValue> responsibleForPartyList = delegator.findList("EmplPositionAndFulfillment", mainConditons, null, null, null, false);
				if (UtilValidate.isNotEmpty(responsibleForPartyList)) {
					
					List<String> responsibleForPartyIdList = EntityUtil.getFieldListFromEntityList(responsibleForPartyList, "employeePartyId", true);
					
					// filter base on postalCode [start]
					
					if (UtilValidate.isNotEmpty(postalCode)) {
						
						List<EntityCondition> conditionList = FastList.newInstance();
						conditionList.add(EntityCondition.makeCondition("countryGeoId", EntityOperator.EQUALS, countryGeoId));
		                conditionList.add(EntityCondition.makeCondition("city", EntityOperator.EQUALS, city));
		                conditionList.add(EntityCondition.makeCondition("postalCode", EntityOperator.EQUALS, postalCode));
		                List<GenericValue> locationAssocList = delegator.findList("UserLocationAssoc", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
		                
		                if (UtilValidate.isNotEmpty(locationAssocList)) {
		                	List<String> locationAssocPartyIdList = EntityUtil.getFieldListFromEntityList(locationAssocList, "partyId", true);
		                	responsibleForPartyIdList.retainAll(locationAssocPartyIdList);
		                }
						
					}
					
					// filter base on postalCode [end]
					
					// filter base on virtualTeam [start]
					
					if (UtilValidate.isNotEmpty(virtualTeamId)) {
						Map<String, Object> virtualTeam = VirtualTeamUtil.getFirstVirtualTeamMember(delegator, virtualTeamId, userLogin.getString("partyId"));
						if (UtilValidate.isNotEmpty(virtualTeam.get("virtualTeamId"))) {
							
							List<Map<String, Object>> virtualTeamMemberList = VirtualTeamUtil.getVirtualTeamMemberList(delegator, virtualTeam.get("virtualTeamId").toString(), null);
			                if (UtilValidate.isNotEmpty(virtualTeamMemberList)) {
			                	List<String> virtualTeamMemberPartyIdList = DataUtil.getFieldListFromMapList(virtualTeamMemberList, "virtualTeamMemberId", true);
			                	//responsibleForPartyIdList = virtualTeamMemberPartyIdList;
			                	responsibleForPartyIdList.retainAll(virtualTeamMemberPartyIdList);
			                }
							
							/*if (virtualTeam.get("securityGroupId").equals("VT_SG_TL")) {
								List<Map<String, Object>> virtualTeamMemberList = VirtualTeamUtil.getVirtualTeamMemberList(delegator, virtualTeam.get("virtualTeamId").toString(), null);
				                if (UtilValidate.isNotEmpty(virtualTeamMemberList)) {
				                	List<String> virtualTeamMemberPartyIdList = DataUtil.getFieldListFromMapList(virtualTeamMemberList, "virtualTeamMemberId", true);
				                	//responsibleForPartyIdList = virtualTeamMemberPartyIdList;
				                	responsibleForPartyIdList.retainAll(virtualTeamMemberPartyIdList);
				                }
							} else {
								responsibleForPartyIdList = new ArrayList<String>() { 
						            { 
						                add(virtualTeam.get("virtualTeamMemberId").toString()); 
						            } 
						        }; 
							}*/
						}
					}
					
					/*if (UtilValidate.isNotEmpty(virtualTeamId)) {
						
						List<Map<String, Object>> virtualTeamMemberList = VirtualTeamUtil.getVirtualTeamMemberList(delegator, virtualTeamId, null);
		                
		                if (UtilValidate.isNotEmpty(virtualTeamMemberList)) {
		                	List<String> virtualTeamMemberPartyIdList = DataUtil.getFieldListFromMapList(virtualTeamMemberList, "virtualTeamMemberId", true);
		                	responsibleForPartyIdList.retainAll(virtualTeamMemberPartyIdList);
		                }
						
					}*/
					
					// filter base on virtualTeam [end]
					
					for (String responsibleForPartyId : responsibleForPartyIdList) {
						
						Map<String, Object> result = new HashMap<String, Object>();
						
						String employeePartyId = responsibleForPartyId;
						
						result.put("partyId", employeePartyId);
						result.put("partyName", PartyHelper.getPartyName(delegator, employeePartyId, false));
						
						results.add(result);
						
						if (userLoginPartyId.equals(employeePartyId)) {
							isUserLoginAlreadyListed = true;
						}
					}
				}
				
				if (!isUserLoginAlreadyListed) {
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("partyId", userLoginPartyId);
					result.put("partyName", PartyHelper.getPartyName(delegator, userLoginPartyId, false));
					results.add(result);
				}
				
				resp.put("results", results);

				
				resp.put(CrmConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				
			} else {
				resp.put(CrmConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
	            resp.put(CrmConstants.RESPONSE_MESSAGE, "Provide required parameters as jobFamily, city, countryGeoId..");
			}
			
		} catch (Exception e) {
			
			resp.put(CrmConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(CrmConstants.RESPONSE_MESSAGE, "Error: "+e.getMessage());
			
			Debug.logError(e.getMessage(), MODULE);
		}
		
		return doJSONResponse(response, resp);
	}
    
    @SuppressWarnings("unchecked")
	public static String getGeoAssocList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);

		String geoId = request.getParameter("geoId");
		String geoAssocTypeId = request.getParameter("geoAssocTypeId");
		Timestamp statusDate = UtilDateTime.nowTimestamp();

		Map<String, Object> resp = new HashMap<String, Object>();

		try {
			
			if (UtilValidate.isNotEmpty(geoId) && UtilValidate.isNotEmpty(geoAssocTypeId)) {
				
				List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
				
				List<EntityCondition> conditions = new ArrayList <EntityCondition>();
				
				conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("geoId", EntityOperator.EQUALS, geoId),
						EntityCondition.makeCondition("geoAssocTypeId", EntityOperator.EQUALS, geoAssocTypeId)
						)
						);
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				List<GenericValue> geoAssocList = delegator.findList("GeoAssocSummary", mainConditons, null, null, null, false);
				if (UtilValidate.isNotEmpty(geoAssocList)) {
					for (GenericValue geoAssoc : geoAssocList) {
						
						Map<String, Object> result = new HashMap<String, Object>();
						
						result.put("geoId", geoAssoc.getString("geoId"));
						result.put("geoIdTo", geoAssoc.getString("geoIdTo"));
						result.put("geoName", geoAssoc.getString("geoName"));
						result.put("geoAssocTypeId", geoAssoc.getString("geoAssocTypeId"));
						
						results.add(result);
					}
				}
				
				resp.put("results", results);

				resp.put(CrmConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				
			} else {
				resp.put(CrmConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
	            resp.put(CrmConstants.RESPONSE_MESSAGE, "Provide required parameters as geoId, geoAssocTypeId..");
			}
			
		} catch (Exception e) {
			
			resp.put(CrmConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(CrmConstants.RESPONSE_MESSAGE, "Error: "+e.getMessage());
			
			Debug.logError(e.getMessage(), MODULE);
		}
		
		return doJSONResponse(response, resp);
	}
    
    /**
     * @author Mahendran T
     * @since 04-10-2018<br>
     * Get the enumeration based on the list type which we requested
     * @param listType (Enumeration List Type)
     * @param userLoginGeoId (Login user country geo id)
     * */
    public static String getEnumerations(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String draw = request.getParameter("draw");
		String start = request.getParameter("start");
		String length = request.getParameter("length");
		String listType = request.getParameter("listType");
		String userLoginGeoId = request.getParameter("userLoginGeoId");
		
		Map < String, Object > returnMap = FastMap.newInstance();
		List<Map<String, Object>> data = FastList.newInstance();
		try {
			int recordsFiltered = 0;
			int recordsTotal = 0;
			if((userLoginGeoId != null && userLoginGeoId.length() > 0) && (listType != null && listType.length() > 0)) {
				List<GenericValue> enumList = new ArrayList<GenericValue>();
				
				EntityFindOptions efo = new EntityFindOptions();
				efo.setDistinct(true);
				
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("listType", EntityOperator.EQUALS, listType),
						EntityCondition.makeCondition("countryCode", EntityOperator.EQUALS, userLoginGeoId)
               			);
				
				GenericValue countryEnumeration = EntityUtil.getFirst( delegator.findList("CountryEnumeration", condition, null, UtilMisc.toList("-createdStamp"), null, false) );
				if (UtilValidate.isNotEmpty(countryEnumeration)) {
					int count = 0;
					enumList = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId",countryEnumeration.getString("enumTypeId")), null, UtilMisc.toList("sequenceId"), efo, false);
					if (enumList != null && enumList.size() > 0) {
						count = EntityUtil.getFieldListFromEntityList(enumList, "enumId", true).size();
						recordsFiltered = count;
						recordsTotal = count;
						
						enumList.forEach(e->{
							Map < String, Object > dataMap = FastMap.newInstance();
							dataMap.put("enumId", e.getString("enumId"));
							dataMap.put("enumCode", e.getString("enumCode"));
							dataMap.put("enumTypeId", e.getString("enumTypeId"));
							dataMap.put("sequenceId", e.getString("sequenceId"));
							dataMap.put("description", e.getString("description"));
							dataMap.put("disabled", e.getString("disabled"));
							data.add(dataMap);
						});
						
					}
				}
				
			}
			
			returnMap.put("data", data);
			returnMap.put("draw", draw);
			returnMap.put("recordsTotal", recordsTotal);
			returnMap.put("recordsFiltered", recordsFiltered);
		} catch (Exception e) {
			Debug.logError("Exception in Get Team Member" + e.getMessage(), MODULE);
			returnMap.put("data", data);
			returnMap.put("draw", draw);
			returnMap.put("recordsTotal", 0);
			returnMap.put("recordsFiltered", 0);
			return AjaxEvents.doJSONResponse(response, returnMap);
		}
		return AjaxEvents.doJSONResponse(response, returnMap);
	
    }
    
    /**
     * @author Mahendran T
     * @since 05-10-2018<br>
     * Create new enumeration
     * @param listType (Enumeration List Type)
     * @param userLoginGeoId (Login user country geo id)
     * @param enumId 
     * @param enumCode
     * @param sequenceId
     * @param description
     * @param disabled
     * */
    public static String createEnumeration(HttpServletRequest request, HttpServletResponse response) {
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	String enumId = request.getParameter("enumId");
    	String listType = request.getParameter("listType");
    	Map<String, Object> result = new HashMap<String,Object>();
    	try {
    		Map<String, Object> context = new HashMap<String, Object>();
    		Enumeration<String> en = request.getParameterNames();

    	    while(en.hasMoreElements()){
    	        String parameterName = en.nextElement();
    	        context.put(parameterName, request.getParameter(parameterName));
    	    }
    	    GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
    	    String countryGeoId = "";
    	    if(userLogin != null && userLogin.size() > 0) {
    	    	countryGeoId = userLogin.getString("countryGeoId");
    	    }
    	    EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("listType", EntityOperator.EQUALS, listType),
					EntityCondition.makeCondition("countryCode", EntityOperator.EQUALS, countryGeoId)
           			);
    	    GenericValue countryEnumeration = EntityUtil.getFirst( delegator.findList("CountryEnumeration", condition, null, UtilMisc.toList("-createdStamp"), null, false) );
    	    if(countryEnumeration != null && countryEnumeration.size() > 0) {
    	    	context.put("enumTypeId", countryEnumeration.getString("enumTypeId"));
    	    }
    		GenericValue enumeration = delegator.findOne("Enumeration", UtilMisc.toMap("enumId",enumId), false);
    		if(enumeration == null || enumeration.size() == 0) {
    			enumeration = delegator.makeValue("Enumeration");
    			enumeration.setPKFields(context);
    			enumeration.setNonPKFields(context);
    			enumeration.create();
    		} else {
    			result.put(CrmConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
    			result.put(CrmConstants.RESPONSE_MESSAGE, "Record already exists");
    		}
    		
    	} catch (Exception e) {
    		//e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);

			result.put(CrmConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(CrmConstants.RESPONSE_MESSAGE, e.getMessage());

			return doJSONResponse(response, result);
		}
    	return doJSONResponse(response, result);
    }
    
    /**
     * @author Mahendran T
     * @since 06-10-2018<br>
     * Update the existing enumeration
     * @param listType (Enumeration List Type)
     * @param userLoginGeoId (Login user country geo id)
     * @param enumId 
     * @param sequenceId
     * @param description
     * @param disabled
     * */
    public static String updateEnumeration(HttpServletRequest request, HttpServletResponse response) {
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	String enumId = request.getParameter("enumId");
    	Map<String, Object> result = new HashMap<String,Object>();
    	try {
    		Map<String, Object> context = new HashMap<String, Object>();
    		Enumeration<String> en = request.getParameterNames();

    	    while(en.hasMoreElements()){
    	        String parameterName = en.nextElement();
    	        context.put(parameterName, request.getParameter(parameterName));
    	    }
    	    
    		GenericValue enumeration = delegator.findOne("Enumeration", UtilMisc.toMap("enumId",enumId), false);
    		if(enumeration != null && enumeration.size() > 0) {
    			enumeration.setNonPKFields(context);
    			enumeration.store();
    		} else {
    			result.put(CrmConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
    			result.put(CrmConstants.RESPONSE_MESSAGE, "Record not exists");
    		}
    		
    	} catch (Exception e) {
    		//e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);

			result.put(CrmConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(CrmConstants.RESPONSE_MESSAGE, e.getMessage());

			return doJSONResponse(response, result);
		}
    	return doJSONResponse(response, result);
    }
    
    /**
     * @author Mahendran T
     * @since 06-10-2018<br>
     * Remove the existing enumeration
     * @param enumId 
     * */
    public static String removeEnumeration(HttpServletRequest request, HttpServletResponse response) {
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	String enumId = request.getParameter("enumId");
    	Map<String, Object> result = new HashMap<String,Object>();
    	try {
    		
    		GenericValue enumeration = delegator.findOne("Enumeration", UtilMisc.toMap("enumId",enumId), false);
    		if(enumeration != null && enumeration.size() > 0) {
    			enumeration.remove();
    		} else {
    			result.put(CrmConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
    			result.put(CrmConstants.RESPONSE_MESSAGE, "Record not exists");
    		}
    		
    	} catch (Exception e) {
    		//e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);

			result.put(CrmConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(CrmConstants.RESPONSE_MESSAGE, e.getMessage());

			return doJSONResponse(response, result);
		}
    	return doJSONResponse(response, result);
    }
    
    @SuppressWarnings("unchecked")
	public static String getGeoAssocState(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);

		String geoIdTo = request.getParameter("geoIdTo");
		String geoAssocTypeId = request.getParameter("geoAssocTypeId");
		Timestamp statusDate = UtilDateTime.nowTimestamp();

		Map<String, Object> resp = new HashMap<String, Object>();

		try {
			
			if (UtilValidate.isNotEmpty(geoIdTo) && UtilValidate.isNotEmpty(geoAssocTypeId)) {
				
				List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
				
				List<EntityCondition> conditions = new ArrayList <EntityCondition>();
				
				conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("geoIdTo", EntityOperator.EQUALS, geoIdTo),
						EntityCondition.makeCondition("geoAssocTypeId", EntityOperator.EQUALS, geoAssocTypeId)
						)
						);
				Map<String, Object> result = new HashMap<String, Object>();
				EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				GenericValue geoAssocList = EntityUtil.getFirst(delegator.findList("GeoAssoc", mainConditons, null, null, null, false));
				if (UtilValidate.isNotEmpty(geoAssocList)) {
						String geoId = geoAssocList.getString("geoId");
						Debug.log("===geoId======="+geoId);
						if(UtilValidate.isNotEmpty(geoId)) {
							GenericValue geo = EntityQuery.use(delegator).from("Geo").where("geoId",geoId).queryOne();
							if(UtilValidate.isNotEmpty(geo)) {
								
								result.put("geoId", geoId);
								result.put("geoName", geo.getString("geoName"));
							}
						}
						results.add(result);
						Debug.log("===results======="+results);
				}
				
				resp.put("results", results);

				resp.put(CrmConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				
			} else {
				resp.put(CrmConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
	            resp.put(CrmConstants.RESPONSE_MESSAGE, "Provide required parameters as geoId, geoAssocTypeId..");
			}
			
		} catch (Exception e) {
			
			resp.put(CrmConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(CrmConstants.RESPONSE_MESSAGE, "Error: "+e.getMessage());
			
			Debug.logError(e.getMessage(), MODULE);
		}
		
		return doJSONResponse(response, resp);
	}
    
    @SuppressWarnings("unchecked")
	public static String getConstituteList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);

		String enumTypeId = request.getParameter("enumTypeId");
		Timestamp statusDate = UtilDateTime.nowTimestamp();
		Debug.log("==enumTypeId==="+enumTypeId);

		Map<String, Object> resp = new HashMap<String, Object>();

		try {
			
			if (UtilValidate.isNotEmpty(enumTypeId)) {
				
				List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
				
				List<EntityCondition> conditions = new ArrayList <EntityCondition>();
				
				conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, enumTypeId)));
				EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				List<GenericValue> constituteList = delegator.findList("Enumeration", mainConditons, null, null, null, false);
				Debug.log("==constituteList==="+constituteList);
				if (UtilValidate.isNotEmpty(constituteList)) {
					for (GenericValue constitute : constituteList) {
						
						Map<String, Object> result = new HashMap<String, Object>();
						
						result.put("enumId", constitute.getString("enumId"));
						result.put("description", constitute.getString("description"));
						
						
						results.add(result);
					}
				}
				Debug.log("==results==="+results);
				resp.put("results", results);

				resp.put(CrmConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				
			} else {
				resp.put(CrmConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
	            resp.put(CrmConstants.RESPONSE_MESSAGE, "Provide required parameters as geoId, geoAssocTypeId..");
			}
			
		} catch (Exception e) {
			
			resp.put(CrmConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(CrmConstants.RESPONSE_MESSAGE, "Error: "+e.getMessage());
			
			Debug.logError(e.getMessage(), MODULE);
		}
		
		return doJSONResponse(response, resp);
	}
    
    @SuppressWarnings("unchecked")
	public static String getPostalCodeDetail(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		HttpSession session = request.getSession(true);

		String postalCode = request.getParameter("postalCode");
		String geoId = request.getParameter("geoId");

		Map<String, Object> resp = new HashMap<String, Object>();
		try {

			Map<String, Object> postalCodeDetail = new HashMap<String, Object>();
 			
			if (UtilValidate.isNotEmpty(postalCode)) {
				
				List<EntityCondition> conditions = new ArrayList <EntityCondition>();
				
				conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("geoName", EntityOperator.EQUALS, postalCode),
						EntityCondition.makeCondition("geoAssocTypeId", EntityOperator.EQUALS, "POSTAL_CODE"),
						EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS, "POSTAL_CODE")
						)
						);
				
				if (UtilValidate.isNotEmpty(geoId)) {
					conditions.add(EntityCondition.makeCondition("geoId", EntityOperator.EQUALS, geoId));
				}
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				GenericValue geoAssoc = EntityUtil.getFirst( delegator.findList("GeoAssocSummary", mainConditons, null, null, null, false) );
				
				if (UtilValidate.isNotEmpty(geoAssoc)) {
					
					postalCodeDetail.put("postalCode", geoAssoc.get("geoName"));
					postalCodeDetail.put("city", geoAssoc.get("geoId"));
					
					GenericValue geoStateAssoc = EntityUtil.getFirst( delegator.findByAnd("GeoAssocSummary", UtilMisc.toMap("geoIdTo", geoAssoc.get("geoId"), "geoAssocTypeId", "COUNTY_CITY"), null, false) );
					if (UtilValidate.isNotEmpty(geoStateAssoc)) {
						postalCodeDetail.put("stateProvinceGeoId", geoStateAssoc.get("geoId"));
					}
					
				}
				
			}

			resp.put("postalCodeDetail", postalCodeDetail);

			resp.put(CrmConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);

		} catch (Exception e) {
			//e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);

			resp.put(CrmConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			resp.put(CrmConstants.RESPONSE_MESSAGE, e.getMessage());

			return doJSONResponse(response, resp);
		}

		return doJSONResponse(response, resp);
	}
    
    /*
	 * Get PartyCampaignDetails in CRM  
	 */
	public static String getPartyCampaignDetails(HttpServletRequest request, HttpServletResponse response) {

		HttpSession session = request.getSession();
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        Map<String, Object> context = UtilHttp.getCombinedMap(request);
        
		String partyId = request.getParameter("partyId");
		try {
			EntityFindOptions efo = new EntityFindOptions();
			efo.setDistinct(true);
			efo.getDistinct();			
			
			List<EntityCondition> partyConditions = new ArrayList <EntityCondition>();
			
			partyConditions.add( EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
					EntityCondition.makeCondition("contactPurposeTypeId", EntityOperator.EQUALS, "LIVE")));
			EntityCondition partyMainConditons = EntityCondition.makeCondition(partyConditions, EntityOperator.AND);
			
			List < GenericValue > getpartyDetails=null;
			
			getpartyDetails = delegator.findList("CampaignContactListParty", partyMainConditons,null, null, efo, false);					
				
			String contactListId = "";
			String campaignName = "";
			String campaignId = "";
			String opened = "";
			String notOpen = "";
			String bounced = "";
			String unSubscribe ="";
			String subscribe ="";
			String clickCount = "";
			String converted = "";
			String campaignTypeId = "";
			String campaignTypeDesc = "";
			String accountId = "";
			String accountName = "";
			for (GenericValue campaignDetails: getpartyDetails) {
				Map< String, Object > importDetails = new HashMap< String, Object >();
				contactListId = campaignDetails.getString("contactListId");
				opened = campaignDetails.getString("opened");
				notOpen = campaignDetails.getString("notOpen");
				bounced = campaignDetails.getString("bounced");
				unSubscribe = campaignDetails.getString("unsubscribed");
				subscribe = campaignDetails.getString("subscribed");
				converted = campaignDetails.getString("converted");
				accountId = campaignDetails.getString("acctPartyId");
				List<EntityCondition> conditions = new ArrayList <EntityCondition>();
				
				conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("contactListId", EntityOperator.EQUALS, contactListId)));
				EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				GenericValue getcampaignId = EntityUtil.getFirst( delegator.findByAnd("MarketingCampaignContactList", UtilMisc.toMap("contactListId", contactListId), null, false) );
				if (getcampaignId !=null){
				 campaignId = getcampaignId.getString("marketingCampaignId");
				}
				GenericValue campNameDetails = delegator.findOne("MarketingCampaign", UtilMisc.toMap("marketingCampaignId",campaignId), false);
				if(campNameDetails != null){
				campaignName = campNameDetails.getString("campaignName");
				campaignTypeId = campNameDetails.getString("campaignTypeId");
				}
				GenericValue campaignClickList = EntityUtil.getFirst(delegator.findByAnd("MarketingCampaignClickedDetails", UtilMisc.toMap("campaignId",campaignId,"partyId",partyId,"linkTypeId","LINK"), null, false));
				if(campaignClickList != null){
				clickCount = campaignClickList.getString("count");
				}
				GenericValue capmaignType = delegator.findOne("CampaignType",UtilMisc.toMap("campaignTypeId",campaignTypeId),false);
                if(UtilValidate.isNotEmpty(capmaignType)){
                    campaignTypeDesc = capmaignType.getString("description");
                }
                if(accountId != null){
                GenericValue acountNameList = delegator.findOne("PartyGroup",UtilMisc.toMap("partyId",accountId),false);
                	accountName = acountNameList.getString("groupName");
                }else{
                	accountName = "";
                }
				importDetails.put("clickCount",clickCount);
				importDetails.put("contactListId", campaignName);
				importDetails.put("campaignId", campaignId);
				importDetails.put("opened",opened);
				importDetails.put("notOpen",notOpen);
				importDetails.put("bounced",bounced);
				importDetails.put("unSubscribe",unSubscribe);
				importDetails.put("subscribe",subscribe);
				importDetails.put("converted",converted);
				importDetails.put("campaignTypeDesc",campaignTypeDesc);
				importDetails.put("accountId",accountId);
				importDetails.put("accountName",accountName);
				results.add(importDetails);
			}	
			Debug.log("Results : " + results, MODULE);
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

	        Map<String, Object> data = new HashMap<String, Object>();
	        data.put("errorMessage", e.getMessage());
	        data.put("errorResult", new ArrayList<Map<String, Object>>());
	        results.add(data);
		}
		return AjaxEvents.doJSONResponse(response, results);
	}
	
    /*
	 * Get AccountCampaignDetails in CRM  
	 */
	public static String getAcctCampaignDetails(HttpServletRequest request, HttpServletResponse response) {

		HttpSession session = request.getSession();
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        Map<String, Object> context = UtilHttp.getCombinedMap(request);
		
		String partyId = request.getParameter("partyId");
		try {
			EntityFindOptions efo = new EntityFindOptions();
			efo.setDistinct(true);
			efo.getDistinct();
				
			List<EntityCondition> partyConditions = new ArrayList <EntityCondition>();
			
			partyConditions.add( EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("acctPartyId", EntityOperator.EQUALS, partyId),
					EntityCondition.makeCondition("contactPurposeTypeId", EntityOperator.EQUALS, "LIVE")));
			EntityCondition partyMainConditons = EntityCondition.makeCondition(partyConditions, EntityOperator.AND);
			
			List < GenericValue > getpartyDetails=null;
			
			getpartyDetails = delegator.findList("CampaignContactListParty", partyMainConditons, UtilMisc.toSet("contactListId"), null, efo, false);					
				
			String contactListId = "";
			String campaignName = "";
			String campaignId = "";
			String opened = "";
			String notOpen = "";
			String bounced = "";
			String unSubscribe ="";
			String subscribe ="";
			String clickCount = "";
			String converted = "";
			String campaignTypeId = "";
			String campaignTypeDesc = "";
			for (GenericValue campaignDetails: getpartyDetails) {
				Map< String, Object > importDetails = new HashMap< String, Object >();
				contactListId = campaignDetails.getString("contactListId");
				List<EntityCondition> conditions = new ArrayList <EntityCondition>();
				
				conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("contactListId", EntityOperator.EQUALS, contactListId)));
				EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				GenericValue getcampaignId = EntityUtil.getFirst( delegator.findByAnd("MarketingCampaignContactList", UtilMisc.toMap("contactListId", contactListId), null, false) );
				if (getcampaignId !=null){
				 campaignId = getcampaignId.getString("marketingCampaignId");
				}
				GenericValue campNameDetails = delegator.findOne("MarketingCampaign", UtilMisc.toMap("marketingCampaignId",campaignId), false);
				if(campNameDetails != null){
				campaignName = campNameDetails.getString("campaignName");
				campaignTypeId = campNameDetails.getString("campaignTypeId");
				}else{
					campaignName = "";
				}
				GenericValue campaignClickList = EntityUtil.getFirst(delegator.findByAnd("MarketingCampaignClickedDetails", UtilMisc.toMap("campaignId",campaignId,"partyId",partyId,"linkTypeId","LINK"), null, false));
				if(campaignClickList != null){
				clickCount = campaignClickList.getString("count");
				}else{
					clickCount = "";
				}
				GenericValue capmaignType = delegator.findOne("CampaignType",UtilMisc.toMap("campaignTypeId",campaignTypeId),false);
	            if(UtilValidate.isNotEmpty(capmaignType)){
	                campaignTypeDesc = capmaignType.getString("description");
	            }else{
	            	campaignTypeDesc = "";
				}
	
	            String groupId = campaignTypeId+"_"+campaignId;
	
				EntityCondition partyMainCond = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, groupId),
	                    EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			
				conditions.add(partyMainCond);
				List < GenericValue > getSegmentDetails =null;
				getSegmentDetails = delegator.findList("CustomFieldPartyClassification", partyMainCond,null, null, null, false);
				if (getSegmentDetails != null && getSegmentDetails.size() > 0) {
	    			for (GenericValue segmentDetails: getSegmentDetails) {
	    				String segGroupId = segmentDetails.getString("groupId");
	    				String customFieldId = segmentDetails.getString("customFieldId");
	    				String customOpen= segGroupId+"_OPEN";
	    				String customNotOpen = segGroupId+"_NOT_OPEN";
	    				String customBounced = segGroupId+"_BOUNCE";
	    				String customUnSubscribe = segGroupId+"_UNSUBSCRIBE";
	    				String customSubscribe = segGroupId+"_SUBSCRIBE";
	    				String customConverted = segGroupId+"_CONVERTED";
	    				if(customOpen.equals(customFieldId)){
	    					opened = "Y";
	    				}
	    				if(customNotOpen.equals(customFieldId)){
	    					notOpen = "Y";
	    				}
	    				if(customBounced.equals(customFieldId)){
	    					bounced = "Y";
	    				}
	    				if(customUnSubscribe.equals(customFieldId)){
	    					unSubscribe = "Y";
	    				}
	    				if(customSubscribe.equals(customFieldId)){
	    					subscribe = "Y";
	    				}
	    				if(customConverted.equals(customFieldId)){
	    					converted = "Y";
	    				}
	    			}
	
				}else{
					 opened = "";
					 notOpen = "";
					 bounced = "";
					 unSubscribe ="";
					 subscribe ="";
					 clickCount = "";
					 converted = "";
				}
				importDetails.put("opened",opened);
				importDetails.put("notOpen",notOpen);
				importDetails.put("bounced",bounced);
				importDetails.put("unSubscribe",unSubscribe);
				importDetails.put("subscribe",subscribe);
				importDetails.put("converted",converted);
				importDetails.put("clickCount",clickCount);
				importDetails.put("contactListId", campaignName);
				importDetails.put("campaignId", campaignId);
				
				importDetails.put("campaignTypeDesc",campaignTypeDesc);
				results.add(importDetails);						
				 opened = "";
				 notOpen = "";
				 bounced = "";
				 unSubscribe ="";
				 subscribe ="";
				 clickCount = "";
				 converted = "";
			}
			Debug.log("Results : " + results, MODULE);
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("errorMessage", e.getMessage());
            data.put("errorResult", new ArrayList<Map<String, Object>>());
            results.add(data);
		}
		return AjaxEvents.doJSONResponse(response, results);
	}
	
    
    /*
	 * Import DND List into Application 
	 */ 
    
    public static String uploadDndFile(HttpServletRequest request,HttpServletResponse response){
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String fileName = null;
		String extension=null;
		File filepath = null;
		try
		{ 
			String ofbizHome = System.getProperty("ofbiz.home");
			String localPath = null;
			String importedFilePath = null;
			fileName = "DNDList_"+ new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			GenericValue systemProperty = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "smartlist","systemPropertyId","filepath").queryOne();
			if(UtilValidate.isNotEmpty(systemProperty)){
				localPath = systemProperty.getString("systemPropertyValue");
			}	
			String dirPath = ofbizHome + localPath;
			if(UtilValidate.isEmpty(dirPath))
			{
				String componentPath = ComponentConfig.getRootLocation("campaign");
				String path = "/smartlist/";
				dirPath = componentPath+path;
				filepath = new File(dirPath);
				filepath.mkdirs();
			}
			else{
				filepath = new File(dirPath);
			}
			Debug.log("ServletFileUpload.isMultipartContent---@M-->"+ServletFileUpload.isMultipartContent(request));
			if(ServletFileUpload.isMultipartContent(request)){
				try {
					List<FileItem> multiparts = new ServletFileUpload(
							new DiskFileItemFactory()).parseRequest(request);
					String fname = null;
					for(FileItem item : multiparts){

						if (item.isFormField()) {
							String name = item.getFieldName();
							 if("extension".equals(name)){
								String value = item.getString();
								extension=value;
							}
						}
					}
					String name = null;
					System.out.println("extension"+extension);
					for(FileItem item : multiparts){
						if(!item.isFormField()){
							name = new File(item.getName()).getName();

							if(name.endsWith(fileName))
							{
								fname = name;
							}
							else
							{
								fname = fileName+"."+extension;; 
							}
							File f = new File(filepath + File.separator + fname);
							if(f.exists())
							{
								f.delete();
							}
							item.write( f);
							importedFilePath = filepath + File.separator + fname;
						}

					}
					
					 
					
			     CSVReader reader = new CSVReader(new FileReader(importedFilePath), ',', '"', 1);
                 List < String[] > records = reader.readAll();
                 if (records != null && records.size() > 0) {
					
					GenericValue storeImportDetails = delegator.makeValue("DbsImportDetails");
					String importId =delegator.getNextSeqId("DbsImportDetails"); 
					storeImportDetails.set("importId",importId);
					storeImportDetails.set("actualFileName",name);
					storeImportDetails.set("processedFileName",fname);
					storeImportDetails.set("processed","N");
					delegator.create(storeImportDetails);
					TransactionUtil.commit();
					
					
					if(fname != null && importId != null ){
					GenericValue systemPropertyScript = delegator.findOne("SystemProperty", UtilMisc.toMap("systemResourceId", "dndImportScript","systemPropertyId","dndScript"), false);
					if(systemPropertyScript != null && systemPropertyScript.size() > 0) {
						String shellScriptPath = systemPropertyScript.getString("systemPropertyValue");
						//String shellScriptPath = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "externalApp","systemPropertyId",processName).queryOne().getString("systemPropertyValue");
						Debug.logInfo("DndScript" +" --shellScriptPath-->"+shellScriptPath, MODULE);
						if (UtilValidate.isNotEmpty(shellScriptPath)) {
							File file = new File(shellScriptPath);
							if (file.exists()) {
								file.setExecutable(true);
								file.setReadable(true);
								file.setWritable(true);

								String cmd = shellScriptPath;
								if(UtilValidate.isNotEmpty(fname)) {
									cmd = cmd.concat(" " + fname);
								}
								
								ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
								Debug.logInfo("execute DND Import List Start :"+ cmd,MODULE);
								Process p = pb.start();
								try {
									
									BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
									StringBuilder builder = new StringBuilder();
									String line = null;
									while ( (line = output.readLine()) != null) {
										builder.append(line);
									}
									String result = builder.toString();
									Debug.logInfo("execute DND Import List output : " + result,MODULE);
									output.close();
									
									BufferedReader output1 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
									StringBuilder errorBuilder = new StringBuilder();
									String errorLine = null;
									while ( (errorLine = output1.readLine()) != null) {
										errorBuilder.append(errorLine);
									}
									String errorResult = errorBuilder.toString();
									Debug.logError("execute DND Import List error output : " + errorResult,MODULE);
									output1.close();
									p.waitFor();
								} catch (InterruptedException  e) {
							      	Debug.logInfo("shellTimer wait for command execution  exception : "+e,MODULE);
								}finally{
								       	p.destroy();
								}
								
							} else {
								Debug.log("execute DND Import script not exist");
							}
						}
					} else {
						Debug.logError("Please configure the script path", MODULE);
						return "error";
					}
					Debug.logInfo("DND Import executed successfully", MODULE);
					}

					request.setAttribute("_EVENT_MESSAGE_", "File Uploaded Successfully");
                  }else {
                      Debug.log("No data available in the file");
                      request.setAttribute("_ERROR_MESSAGE_", "File Upload Failed, No Records Found");
                      return "error";
                  }
				} catch (Exception ex) {
					request.setAttribute("_ERROR_MESSAGE_", "File Upload Failed due to " + ex);
					return "error";
				}
			}else{

				request.setAttribute("_ERROR_MESSAGE_","Servlet only handles file upload request");
				return "error";
			} 
		} 
		catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
		return "success";
	}
    
    
    /*
	 * Get DND Import Details 
	 */
	public static String dndImportDetails(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		try {
			List < GenericValue > dndImportList = delegator.findList("DbsImportDetails", null, null, null, null, false).stream().collect(Collectors.toList());
			
			if (dndImportList != null && dndImportList.size() > 0) {				
				String importId = "";
				String actualFileName = "";
				String processed = "";
				String totalCount = "";
				int totalError = 0;
				for (GenericValue importDnd: dndImportList) {
					Map< String, Object > importDetails = new HashMap< String, Object >();
					importId = importDnd.getString("importId");
					actualFileName = importDnd.getString("actualFileName");
					processed = importDnd.getString("processed");
					totalCount = importDnd.getString("totalCount");
					List<EntityCondition> conditions = new ArrayList <EntityCondition>();
					conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("importId", EntityOperator.EQUALS, importId)));
					EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
					List < GenericValue > dnderrorCount = delegator.findList("DbsDndErrorLog", mainConditons, null,null,null, false);
					totalError = dnderrorCount.size();	
					importDetails.put("importId", importId);
					importDetails.put("actualFileName", actualFileName);
					importDetails.put("processed", processed);
					importDetails.put("totalCount", totalCount);
					importDetails.put("totalError", totalError);
					results.add(importDetails);
				}
			}
			Debug.log("Results : " + results, MODULE);
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("errorMessage", e.getMessage());
            data.put("errorResult", new ArrayList<Map<String, Object>>());
            results.add(data);
		}
		return AjaxEvents.doJSONResponse(response, results);
	}
	
    /*
	 * Get DND Error Log Details 
	 */
	public static String dndErrorLogsDetails(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		
		String importId = request.getParameter("importId");
		String searchParam = request.getParameter("search");
		
		List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		try {
			
			List<EntityCondition> conditions = new ArrayList <EntityCondition>();
			conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("importId", EntityOperator.EQUALS, importId)));
			if(searchParam != null || searchParam !=""){
				conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
					EntityCondition.makeCondition("importId",EntityOperator.LIKE,"%"+searchParam+"%"),
					EntityCondition.makeCondition("errorId",EntityOperator.LIKE,"%"+searchParam+"%"),
					EntityCondition.makeCondition("dndNumber",EntityOperator.LIKE,"%"+searchParam+"%"),
					EntityCondition.makeCondition("dndIndicator",EntityOperator.LIKE,"%"+searchParam+"%"),
					EntityCondition.makeCondition("codeDescription",EntityOperator.LIKE,"%"+searchParam+"%")));
			}
			EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			EntityFindOptions efo = new EntityFindOptions();
			efo.setDistinct(true);
			efo.getDistinct();
			
			List < GenericValue > dndImportList = delegator.findList("DndErrorRecordData", mainConditons, null, null, efo, false);
			
			if (dndImportList != null && dndImportList.size() > 0) {
				String errorId = "";
				String dndNumber = "";
				String dndIndicator = "";
				String codeDescription = "";
				for (GenericValue importDnd: dndImportList) {
					Map< String, Object > importDetails = new HashMap< String, Object >();
					importId = importDnd.getString("importId");
					errorId = importDnd.getString("errorId");
					dndNumber = importDnd.getString("dndNumber");
					dndIndicator = importDnd.getString("dndIndicator");	
					codeDescription = importDnd.getString("codeDescription");
					importDetails.put("importId", importId);
					importDetails.put("errorId", errorId);
					importDetails.put("codeDescription", codeDescription);
					importDetails.put("dndNumber", dndNumber);
					importDetails.put("dndIndicator", dndIndicator);
					results.add(importDetails);
				}
			}
			Debug.log("Results : " + results, MODULE);
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("errorMessage", e.getMessage());
            data.put("errorResult", new ArrayList<Map<String, Object>>());
            results.add(data);
		}
		return AjaxEvents.doJSONResponse(response, results);
	}
	
	public static String dndPhoneNumberValidation(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		String primaryPhoneNumber = request.getParameter("primaryPhoneNumber");
		String secondaryPhoneNumber = request.getParameter("secondaryPhoneNumber");

		String dndPrimaryPhoneStatus = "N";
		String dndSecondaryPhoneStatus = "N";

		Map<String, Object> resp = new HashMap<String, Object>();
		try {

			if(UtilValidate.isNotEmpty(primaryPhoneNumber)) {
				Map<String, Object> dndPrimaryPhoneStatusMp = DataUtil.getDndStatus(delegator, primaryPhoneNumber);
				dndPrimaryPhoneStatus = (String) dndPrimaryPhoneStatusMp.get("dndStatus");
			}

			if(UtilValidate.isNotEmpty(secondaryPhoneNumber)) {
				Map<String, Object> dndSecondaryPhoneStatusMp = DataUtil.getDndStatus(delegator, secondaryPhoneNumber);
				dndSecondaryPhoneStatus = (String) dndSecondaryPhoneStatusMp.get("dndStatus");
			}
			resp.put("dndPrimaryPhoneStatus", dndPrimaryPhoneStatus);
			resp.put("dndSecondaryPhoneStatus", dndSecondaryPhoneStatus);

		} catch (Exception e) {
			//e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			return doJSONResponse(response, resp);
		}

		return doJSONResponse(response, resp);
	}
	
    public static String rmReassignFromLeadAjax(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
        java.sql.Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        Locale locale = UtilHttp.getLocale(request);
        
        try {
            List<String> partyIdList = UtilCommon.getArrayToList((String) request.getParameter("partyList"));
            String reAssignPartyId = (String) request.getParameter("reAssignPartyId");
            if(UtilValidate.isNotEmpty(reAssignPartyId) && partyIdList != null && partyIdList.size() > 0) {
                for(String leadId : partyIdList) { 
                    Boolean validate = true;
                    EntityCondition conditionPR = EntityCondition.makeCondition(UtilMisc.toList(
                        EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, leadId),
                        EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "LEAD"),
                        EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT_MANAGER"),
                        EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
                        EntityUtil.getFilterByDateExpr()), EntityOperator.AND);

                    List<GenericValue> oldResponsibleForList = EntityQuery.use(delegator).from("PartyRelationship").where(conditionPR).queryList();
                    if (UtilValidate.isNotEmpty(oldResponsibleForList)) {
                        for (GenericValue partyRelationship: oldResponsibleForList) {
                            if(!reAssignPartyId.equals(partyRelationship.getString("partyIdTo"))) {
                                partyRelationship.set("thruDate", nowTimestamp);
                                partyRelationship.store();
                            
                                List<Map<String, Object>> validationAuditLogList = new ArrayList<Map<String, Object>>();
                                validationAuditLogList.add(WriterUtil.prepareValidationAudit(null, "rmPartyId", partyRelationship.getString("partyIdTo"), reAssignPartyId, userLogin.getString("userLoginId"), ValidationAuditType.VAT_RM_REASSIGN, "Reassign "));
                                String pkCombinedValueText = leadId + "::" + leadId;
                                WriterUtil.writeValidationAudit(delegator, pkCombinedValueText, validationAuditLogList);

                            } else {
                                validate = false;
                            }
                        }
                    }
                    
                    if(validate) {
                        conditionPR = EntityCondition.makeCondition(UtilMisc.toList(
                            EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, leadId),
                            EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, reAssignPartyId),
                            EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "LEAD"),
                            EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
                            EntityUtil.getFilterByDateExpr()
                        ),EntityOperator.AND);

                        GenericValue responsibleFor = EntityUtil.getFirst( delegator.findList("PartyRelationship", conditionPR, null, null, null, true) );
                        if (responsibleFor == null || responsibleFor.size() < 1) {
                            GenericValue partyRelationshipcreate = delegator.makeValue("PartyRelationship");
                            partyRelationshipcreate.set("partyIdFrom", leadId);
                            partyRelationshipcreate.set("partyIdTo", reAssignPartyId);
                            partyRelationshipcreate.set("roleTypeIdFrom", "LEAD");
                            partyRelationshipcreate.set("roleTypeIdTo", "ACCOUNT_MANAGER");
                            partyRelationshipcreate.set("securityGroupId", "ACCOUNT_OWNER");
                            partyRelationshipcreate.set("fromDate", nowTimestamp);
                            partyRelationshipcreate.set("partyRelationshipTypeId", "RESPONSIBLE_FOR");
                            partyRelationshipcreate.set("createdByUserLoginId", userLogin.get("userLoginId"));
                            partyRelationshipcreate.create();
                            
                        }
                        
                        GenericValue partySupplementalData = EntityUtil.getFirst( delegator.findByAnd("PartySupplementalData", UtilMisc.toMap("partyId", leadId), null, false) );
                        if (UtilValidate.isNotEmpty(partySupplementalData)) {
                        	partySupplementalData.put("leadAssignBy", userLogin.getString("partyId"));
                        	partySupplementalData.put("leadAssignTo", reAssignPartyId);
                        	partySupplementalData.store();
                        }
                    }
                }
            }
        } catch (GenericEntityException e) {
            Debug.logError("Exception in Update Person Responsible For" + e.getMessage(), MODULE);
            return doJSONResponse(response, UtilMisc.toMap("message", UtilProperties.getMessage("crmUiLabels", "reassignProcessFailed", locale)));
        }
        return doJSONResponse(response, UtilMisc.toMap("message", UtilProperties.getMessage("crmUiLabels", "rmSuccessfullyReassign", locale), "code", "200"));
    }
    
    @SuppressWarnings("unchecked")
	public static String getVirtualTeamRM(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);

		String teamId = request.getParameter("teamId");

		Map<String, Object> resp = new HashMap<String, Object>();

		try {
			
			if (UtilValidate.isNotEmpty(teamId)) {
								
				List<Map<String, Object>> teamMember = VirtualTeamUtil.getVirtualTeamMemberList(delegator, teamId, null);
								
				resp.put("results", teamMember);

				resp.put(CrmConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				
			} else {
				resp.put(CrmConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
	            resp.put(CrmConstants.RESPONSE_MESSAGE, "Provide required parameters as teamId..");
			}
			
		} catch (Exception e) {
			
			resp.put(CrmConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(CrmConstants.RESPONSE_MESSAGE, "Error: "+e.getMessage());
			
			Debug.logError(e.getMessage(), MODULE);
		}
		
		return doJSONResponse(response, resp);
	}
    /**
	 * Gets the list of RMs
	 *
	 * @param delegator the delegator
	 * @param city the city
	 * @param country the country
	 * @return the list of r ms  in given region
	 */
	public List<GenericValue> getRMs(Delegator delegator, String city, String country) {

		List<GenericValue> responsibleForPartyList = new ArrayList<>();
		List<EntityCondition> conditions = new ArrayList<EntityCondition>();
		List<String> fieldsToSelect = new ArrayList<String>();
		List<String> orderBy = new ArrayList<String>();
		List<String> roleType = UtilMisc.toList("DBS_RM", "DBS_BH","DBS_SH","DBS_RH","DBS_CENTRAL");
        
		DynamicViewEntity dynamicView = new DynamicViewEntity();
		dynamicView.addMemberEntity("EP", "EmplPosition");
		dynamicView.addAlias("EP", "emplPositionId");
		dynamicView.addAlias("EP", "countryGeoId");
		dynamicView.addAlias("EP", "city");
		dynamicView.addAlias("EP", "emplPositionTypeId");
		conditions.add(EntityCondition.makeCondition("emplPositionTypeId", EntityOperator.IN, roleType));
		
		/*if(UtilValidate.isNotEmpty(country)){
			conditions.add(EntityCondition.makeCondition("countryGeoId", EntityOperator.EQUALS, country));
		}
		
		if(UtilValidate.isNotEmpty(city)){
			conditions.add(EntityCondition.makeCondition("city", EntityOperator.EQUALS, city));
		}*/
		
		dynamicView.addMemberEntity("EPF", "EmplPositionFulfillment");
		dynamicView.addAlias("EPF", "emplPositionId");
		dynamicView.addAlias("EPF", "partyId");
		dynamicView.addViewLink("EP", "EPF", Boolean.FALSE, ModelKeyMap.makeKeyMapList("emplPositionId"));
		
		dynamicView.addMemberEntity("PER", "Person");
		dynamicView.addViewLink("EPF", "PER", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));
		dynamicView.addAlias("PER", "firstName");
		dynamicView.addAlias("PER", "lastName");
		
		fieldsToSelect.add("firstName");
		fieldsToSelect.add("lastName");
		fieldsToSelect.add("partyId");
		
		orderBy.add("firstName");
		
		
		
		
		try {
//			EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
//			responsibleForPartyList = delegator.findList("EmplPositionAndFulfillment", mainConditons, null, null, null,
//					false);
			
			responsibleForPartyList = EntityQuery.use(delegator).select(UtilMisc.toSet(fieldsToSelect)).from(dynamicView).where(conditions)
			.cursorScrollInsensitive().distinct().orderBy(orderBy).queryList();
			
		} catch (GenericEntityException e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
		return responsibleForPartyList;
	}
	
    public static List listSortingString(List<Object> inpList, String orderBy, String order) { 
    	if(order.equals("DESC")) {
    		java.util.Collections.sort(inpList, new java.util.Comparator<Object>() {
			    public int compare(Object row1, Object row2) {
			    	Map<String,Object> r1 = (Map<String,Object>) row1;
			    	Map<String,Object> r2 = (Map<String,Object>) row2;
			    	String id1 = (String) r1.get(orderBy);
			    	String id2 = (String) r2.get(orderBy);
			    	return id2.compareTo(id1);
			    }
			});
		}else {
			java.util.Collections.sort(inpList, new java.util.Comparator<Object>() {
			    public int compare(Object row1, Object row2) {
			    	Map<String,Object> r1 = (Map<String,Object>) row1;
			    	Map<String,Object> r2 = (Map<String,Object>) row2;
			    	String id1 = (String) r1.get(orderBy);
			    	String id2 = (String) r2.get(orderBy);
			    	return id1.compareTo(id2);
			    }
			});
		}
    	return inpList;
    }
    
    public static List listSortingInt(List<Object> inpList, String orderBy, String order) { 
    	if(order.equals("DESC")) {
    		java.util.Collections.sort(inpList, new java.util.Comparator<Object>() {
			    public int compare(Object row1, Object row2) {
			    	Map<String,Object> r1 = (Map<String,Object>) row1;
			    	Map<String,Object> r2 = (Map<String,Object>) row2;
			    	Integer id1 = (Integer) r1.get(orderBy);
			    	Integer id2 = (Integer) r2.get(orderBy);
			    	return id2.compareTo(id1);
			    }
			});
		}else {
			java.util.Collections.sort(inpList, new java.util.Comparator<Object>() {
			    public int compare(Object row1, Object row2) {
			    	Map<String,Object> r1 = (Map<String,Object>) row1;
			    	Map<String,Object> r2 = (Map<String,Object>) row2;
			    	Integer id1 = (Integer) r1.get(orderBy);
			    	Integer id2 = (Integer) r2.get(orderBy);
			    	return id1.compareTo(id2);
			    }
			});
		}
    	return inpList;
    }
    
    public static List listSortingDate(List<Object> inpList, String orderBy, String order) { 
    	if(order.equals("DESC")) {
    		java.util.Collections.sort(inpList, new java.util.Comparator<Object>() {
			    public int compare(Object row1, Object row2) {
			    	Map<String,Object> r1 = (Map<String,Object>) row1;
			    	Map<String,Object> r2 = (Map<String,Object>) row2;
			    	java.sql.Timestamp id1 = (java.sql.Timestamp) r1.get(orderBy);
			    	java.sql.Timestamp id2 = (java.sql.Timestamp) r2.get(orderBy);
			    	return id2.compareTo(id1);
			    }
			});
		}else {
			java.util.Collections.sort(inpList, new java.util.Comparator<Object>() {
			    public int compare(Object row1, Object row2) {
			    	Map<String,Object> r1 = (Map<String,Object>) row1;
			    	Map<String,Object> r2 = (Map<String,Object>) row2;
			    	java.sql.Timestamp id1 = (java.sql.Timestamp) r1.get(orderBy);
			    	java.sql.Timestamp id2 = (java.sql.Timestamp) r2.get(orderBy);
			    	return id1.compareTo(id2);
			    }
			});
		}
    	return inpList;
    }
    
    public static String findCustomers(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
        java.sql.Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        Locale locale = UtilHttp.getLocale(request);
        //List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> context = UtilHttp.getCombinedMap(request);
    	try {
    		ModelService findCustomerService = dispatcher.getDispatchContext().getModelService("crm.findCustomers");
            Map<String, Object> inputCxt = findCustomerService.makeValid(context, ModelService.IN_PARAM);
            Map<String, Object> callResult = dispatcher.runSync("crm.findCustomers", inputCxt);
    		if(ServiceUtil.isSuccess(callResult)) {
    			Set<String> fieldsToSelect = new TreeSet<String>();
    			fieldsToSelect.add("departmentName");
    			fieldsToSelect.add("importantNote");
    			fieldsToSelect.add("primaryPostalAddressId");
    			fieldsToSelect.add("primaryEmailId");
    			fieldsToSelect.add("primaryTelecomNumberId");
    			
    			@SuppressWarnings("unchecked")
				List<GenericValue> partyList = (List<GenericValue>) callResult.get("partyList");
    			Map<String, Object> data = new HashMap<String, Object>();
    			List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
    			if(UtilValidate.isNotEmpty(partyList)) {	
    				for(GenericValue partyGv : partyList) {
    					Map<String, Object> partyData = new HashMap<String, Object>();
    					partyData.putAll(partyGv);
    					String birthDate = org.fio.admin.portal.util.DataUtil.convertDateTimestamp(partyGv.getString("birthDate"), new SimpleDateFormat("dd/MM/yyyy"), "date", DateTimeTypeConstant.STRING);
    					partyData.put("birthDate", birthDate);
    					String createdDate = org.fio.admin.portal.util.DataUtil.convertDateTimestamp(partyGv.getString("createdDate"), new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"), "date", DateTimeTypeConstant.STRING);
    					partyData.put("createdDate", createdDate);
    					String partyId = partyGv.getString("partyId");
    					GenericValue partySupplementalData = EntityQuery.use(delegator).select(fieldsToSelect).from("PartySupplementalData").where("partyId", partyId).queryFirst();
    					if(UtilValidate.isNotEmpty(partySupplementalData)) {
    						partyData.putAll(partySupplementalData);
    						
    						//get primary postal address
    						String isPostalDisplay = UtilValidate.isNotEmpty(context.get("isPostalDisplay")) ? (String) context.get("isPostalDisplay") : "N";
    						if("Y".equals(isPostalDisplay)) {
    							String primaryPostalAddressId = partySupplementalData.getString("primaryPostalAddressId");
    							Set<String> addressFields = new TreeSet<String>();
    							addressFields.add("toName");
    							addressFields.add("attnName");
    							addressFields.add("address1");
    							addressFields.add("address2");
    							addressFields.add("city");
    							addressFields.add("postalCode");
    							addressFields.add("postalCodeExt");
    							addressFields.add("countryGeoId");
    							addressFields.add("stateProvinceGeoId");
    							EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
										EntityCondition.makeCondition("contactMechId",EntityOperator.EQUALS,primaryPostalAddressId),
										EntityCondition.makeCondition(EntityOperator.OR,
												EntityCondition.makeCondition("addressValidInd",EntityOperator.EQUALS,"Y"),
												EntityCondition.makeCondition("addressValidInd",EntityOperator.EQUALS,null)
												)
										);
    							GenericValue postalAddress = EntityQuery.use(delegator).select(addressFields).where("PostalAddress").where(condition).queryFirst();
    							if(UtilValidate.isNotEmpty(postalAddress)) {
    								partyData.putAll(postalAddress);
    								//get country description
    								GenericValue countryGeo = EntityQuery.use(delegator).select("geoName").from("Geo").where("geoId",postalAddress.getString("countryGeoId"),"geoTypeId","COUNTRY").queryFirst();
    								if(UtilValidate.isNotEmpty(countryGeo)) {
    									partyData.put("country", countryGeo.getString("geoName"));
    								}
    								//get state description
    								EntityCondition condition1 = EntityCondition.makeCondition(EntityOperator.AND,
    										EntityCondition.makeCondition("geoId",EntityOperator.EQUALS,postalAddress.getString("stateProvinceGeoId")),
    										EntityCondition.makeCondition(EntityOperator.OR,
    												EntityCondition.makeCondition("geoTypeId",EntityOperator.EQUALS,"STATE"),
    												EntityCondition.makeCondition("geoTypeId",EntityOperator.EQUALS,"PROVINCE")
    												)
    										);
    								GenericValue stateGeo = EntityQuery.use(delegator).select("geoName").from("Geo").where(condition1).queryFirst();
    								if(UtilValidate.isNotEmpty(stateGeo)) {
    									partyData.put("state", stateGeo.getString("geoName"));
    								}
    							}
    						}
    						String isEmailDisplay = UtilValidate.isNotEmpty(context.get("isEmailDisplay")) ? (String) context.get("isEmailDisplay") : "Y";
    						if("Y".equals(isEmailDisplay)) {
    							String primaryEmailId = partySupplementalData.getString("primaryEmailId");
    							GenericValue contactMech = EntityQuery.use(delegator).select("infoString").from("ContactMech").where("contactMechId",primaryEmailId).queryFirst();
    							if(UtilValidate.isNotEmpty(contactMech)) {
    								EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
    										EntityCondition.makeCondition("contactMechId",EntityOperator.EQUALS,primaryEmailId),
    										EntityCondition.makeCondition(EntityOperator.OR,
    												EntityCondition.makeCondition("emailValidInd",EntityOperator.EQUALS,"Y"),
    												EntityCondition.makeCondition("emailValidInd",EntityOperator.EQUALS,null)
    												)
    										);
    								GenericValue partyContactMech = EntityQuery.use(delegator).from("PartyContactMech").where(condition).queryFirst();
    								if(UtilValidate.isNotEmpty(partyContactMech))
    									partyData.putAll(contactMech);
    							}
    						}
    						
    						String isPhonesDisplay = UtilValidate.isNotEmpty(context.get("isPhonesDisplay")) ? (String) context.get("isPhonesDisplay") : "Y";
    						if("Y".equals(isPhonesDisplay)) {
    							String primaryTelecomNumberId = partySupplementalData.getString("primaryTelecomNumberId");
    							Set<String> phoneFields = new TreeSet<String>();
    							phoneFields.add("countryCode");
    							phoneFields.add("areaCode");
    							phoneFields.add("contactNumber");
    							phoneFields.add("askForName");
    							EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
										EntityCondition.makeCondition("contactMechId",EntityOperator.EQUALS,primaryTelecomNumberId),
										EntityCondition.makeCondition(EntityOperator.OR,
												EntityCondition.makeCondition("phoneValidInd",EntityOperator.EQUALS,"Y"),
												EntityCondition.makeCondition("phoneValidInd",EntityOperator.EQUALS,null)
												)
										);
    							GenericValue telecomNumber = EntityQuery.use(delegator).select(phoneFields).from("TelecomNumber").where(condition).queryFirst();
    							if(UtilValidate.isNotEmpty(telecomNumber)) {
    								partyData.putAll(telecomNumber);
    							}
    						}
    					}
    					dataList.add(partyData);
    				}
    			}
    			result.put("data", dataList);
    			result.put("responseMessage", "success");
    			result.put("successMessage", "Data Loaded successfully.");
    			//result.add(data);
    		} else {
    			Map<String, Object> data = new HashMap<String, Object>();
    			result.put("errorMessage", ServiceUtil.getErrorMessage(callResult));
    			result.put("responseMessage", "error");
    			result.put("data", new ArrayList<Map<String, Object>>());
                //result.add(data);
    		}
    	} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			result.put("errorMessage", e.getMessage());
			result.put("responseMessage", "error");
			result.put("data", new ArrayList<Map<String, Object>>());
            //result.add(data);
		}
    	/*
    	Gson gson = new Gson();
    	String jsonRep = gson.toJson(result);
        return doJSONResponse(response, jsonRep);
        */
    	return doJSONResponse(response, result);
    }
}
