package org.fio.crm.leads;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fio.crm.util.LoginFilterUtil;
import org.fio.crm.export.ExportUtil;
import org.fio.crm.export.ExportWrapper;
import org.fio.crm.export.ExporterFacade;
import org.fio.crm.party.PartyHelper;
import org.fio.crm.util.DataHelper;
import org.fio.crm.util.DataUtil;
import org.fio.crm.util.ResponseUtils;
import org.fio.crm.util.UtilCommon;
import org.fio.crm.util.VirtualTeamUtil;
import org.fio.crm.util.ValidatorUtil;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javolution.util.FastList;
import javolution.util.FastMap;

public class LeadEvents {
	private static final String module = LeadEvents.class.getName();

	@SuppressWarnings("unchecked")
	public static String exportLead(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {

		HttpSession session = request.getSession();
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		String exportType = request.getParameter("exportType");
		List<String> selectedFields = UtilCommon.getArrayToList(request.getParameter("selectedFields"));
		String partyId = request.getParameter("leadId");
		String companyName = request.getParameter("companyName");
		List<String> location = UtilCommon.getArrayToList(request.getParameter("location"));
		List<String> leadStatus = UtilCommon.getArrayToList(request.getParameter("leadStatus"));
		List<String> leadSubStatus = UtilCommon.getArrayToList(request.getParameter("leadSubStatus"));
		List<String> source = UtilCommon.getArrayToList(request.getParameter("leadSource"));
		List<String> leadAssignedTo = UtilCommon.getArrayToList(request.getParameter("leadAssignedTo"));
		List<String> userManager = UtilCommon.getArrayToList(request.getParameter("userManager"));
		String virtualTeamId = "";
		List<String> RMRoleList = UtilCommon.getArrayToList(request.getParameter("RMRoleList"));

		Map<String, Object> returnMap = FastMap.newInstance();
		List<Object> findList = FastList.newInstance();
		String returnMessage = "success";

		Debug.log("selectedFields ==> " + selectedFields);
		Debug.log("companyName ==> " + companyName);
		Debug.log("leadStatus ==> " + leadStatus);
		Debug.log("leadSource ==> " + source);
		Debug.log("RMRoleList ==> " + RMRoleList);

		try {
			List<String> partyIdsTo = new LinkedList<String>();
			if (UtilValidate.isNotEmpty(RMRoleList)) {
				for (String partyIdTo : RMRoleList) {
					Debug.log("==partyIdTo====" + partyIdTo);
					EntityCondition searchConditions = EntityCondition.makeCondition(
							EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyIdTo),
							EntityUtil.getFilterByDateExpr());
					List<GenericValue> existingRelationship = delegator.findList("PartyRelationship", searchConditions,
							null, null, null, false);
					if (UtilValidate.isNotEmpty(existingRelationship)) {
						for (GenericValue partyRelation : existingRelationship) {
							String partyIds = partyRelation.getString("partyIdFrom");
							Debug.log("==partyIds====" + partyIds);
							partyIdsTo.add(partyIds);
						}
					}

				}
			}

			Debug.log("==partyIdsTo====" + partyIdsTo);

			List<EntityCondition> conditions = new ArrayList<EntityCondition>();

			/** construct role conditions */
			EntityCondition roleTypeCondition = EntityCondition.makeCondition(
					UtilMisc.toList(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "LEAD"),
							EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "LEAD_CONVERTED")),
					EntityOperator.AND);
			conditions.add(roleTypeCondition);

			EntityCondition partyStatusCondition = EntityCondition
					.makeCondition(
							UtilMisc.toList(
									EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL,
											"PARTY_DISABLED"),
									EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)),
							EntityOperator.OR);

			conditions.add(partyStatusCondition);
			conditions.add(EntityUtil.getFilterByDateExpr());
			if (UtilValidate.isNotEmpty(RMRoleList)) {
				EntityCondition teamMemberCondition = EntityCondition.makeCondition("partyId", EntityOperator.IN,
						partyIdsTo);
				conditions.add(teamMemberCondition);
			}

			if (UtilValidate.isNotEmpty(partyId)) {
				EntityCondition partyCondition = EntityCondition.makeCondition("partyId", EntityOperator.LIKE,
						"%" + partyId + "%");
				conditions.add(partyCondition);
			}

			if (UtilValidate.isNotEmpty(companyName)) {
				EntityCondition companyNameCondition = EntityCondition.makeCondition("companyName", EntityOperator.LIKE,
						"%" + companyName + "%");
				conditions.add(companyNameCondition);
			}

			if (UtilValidate.isNotEmpty(leadStatus)) {
				EntityCondition condition = EntityCondition.makeCondition("statusId", EntityOperator.IN, leadStatus);
				conditions.add(condition);
			}

			if (UtilValidate.isNotEmpty(location)) {
				EntityCondition locationCondition = EntityCondition.makeCondition("paCity", EntityOperator.IN,
						location);
				conditions.add(locationCondition);
			}

			if (UtilValidate.isNotEmpty(leadAssignedTo)) {
				EntityCondition condition = EntityCondition.makeCondition("leadAssignTo", EntityOperator.IN,
						leadAssignedTo);
				conditions.add(condition);
			}

			/** Login Based lead Filter */
			String userLoginId = userLogin.getString("userLoginId");
			List<String> multiTeamLists = new ArrayList<String>();
			Boolean isTeamLead = true;
			if (LoginFilterUtil.checkEmployeePosition(delegator, userLoginId)) {

				Map<String, Object> dataSecurityMetaInfo = (Map<String, Object>) session
						.getAttribute("dataSecurityMetaInfo");
				if (ResponseUtils.isSuccess(dataSecurityMetaInfo)) {

					List<String> lowerPositionPartyIds = (List<String>) dataSecurityMetaInfo
							.get("lowerPositionPartyIds");
					if (UtilValidate.isNotEmpty(lowerPositionPartyIds)) {

						List<EntityCondition> securityConditions = new ArrayList<EntityCondition>();

						Map<String, Object> virtualTeam = VirtualTeamUtil.getFirstVirtualTeamMember(delegator,
								virtualTeamId, userLogin.getString("partyId"));

						if (UtilValidate.isEmpty(virtualTeam.get("virtualTeamId"))) {
							securityConditions.add(EntityCondition.makeCondition(EntityOperator.AND,
									EntityCondition.makeCondition("partyIdTo", EntityOperator.IN,
											lowerPositionPartyIds),
									EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS,
											"RESPONSIBLE_FOR"),
									EntityUtil.getFilterByDateExpr()));
						}

						if (UtilValidate.isNotEmpty(userLogin)) {
							securityConditions.add(EntityCondition.makeCondition(
									UtilMisc.toList(EntityCondition.makeCondition("uploadedByUserLoginId",
											EntityOperator.EQUALS, userLogin.getString("userLoginId"))),
									EntityOperator.OR));
						}

						/** virtual team [start] */
						securityConditions.add(EntityCondition.makeCondition(EntityCondition
								.makeCondition("leadAssignTo", EntityOperator.EQUALS, userLogin.getString("partyId"))));
						virtualTeamId = UtilValidate.isNotEmpty(virtualTeamId) ? virtualTeamId
								: (String) virtualTeam.get("virtualTeamId");
						List<Map<String, Object>> virtualTeamMemberList = VirtualTeamUtil
								.getVirtualTeamMemberList(delegator, null, userLogin.getString("partyId"));
						if (UtilValidate.isNotEmpty(virtualTeamMemberList)) {
							List<String> virtualTeamIdAsLeadList = VirtualTeamUtil.getVirtualTeamIds(delegator,
									userLogin.getString("partyId"));
							Debug.log("virtualTeamIdAsLeadList  => " + virtualTeamIdAsLeadList);
							if (UtilValidate.isNotEmpty(virtualTeamIdAsLeadList)) {
								List<String> multiTeamMembers = new ArrayList<String>();
								Set<String> virtualTeamMemberPartyIdList = new HashSet<String>();
								for (String vtId : virtualTeamIdAsLeadList) {
									List<String> roles = DataUtil.getLoginRole(delegator,
											userLogin.getString("partyId"), vtId);
									if (roles.contains("VT_SG_TL")) {
										List<Map<String, Object>> teamMemberList = VirtualTeamUtil
												.getVirtualTeamMemberList(delegator, vtId, null);
										Debug.log("teamMemberList  => " + teamMemberList);
										for (Map<String, Object> memberList : teamMemberList) {
											multiTeamLists.add((String) memberList.get("virtualTeamMemberId"));
										}
										virtualTeamMemberPartyIdList.addAll(DataUtil
												.getFieldListFromMapList(teamMemberList, "virtualTeamMemberId", true));
										multiTeamMembers.add(vtId);
									}
								}
								Debug.log(" multiTeamMembers  => " + multiTeamMembers + "\n multiTeamLists  => "
										+ multiTeamLists);
								securityConditions.add(EntityCondition.makeCondition("virtualTeamId", EntityOperator.IN,
										multiTeamMembers));
								if (UtilValidate.isNotEmpty(virtualTeamMemberPartyIdList)) {
									securityConditions.add(EntityCondition.makeCondition(EntityOperator.AND,
											EntityCondition.makeCondition("partyIdTo", EntityOperator.IN,
													virtualTeamMemberPartyIdList),
											EntityCondition.makeCondition("partyRelationshipTypeId",
													EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
											EntityUtil.getFilterByDateExpr()));
								}
							}

							Set<String> virtualTeamIdAsMemberList = VirtualTeamUtil
									.getVirtualTeamIds(virtualTeamMemberList, "VT_SG_TL", false);
							if (UtilValidate.isNotEmpty(virtualTeamIdAsMemberList)) {
								securityConditions.add(EntityCondition.makeCondition(UtilMisc.toList(
										EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS,
												userLogin.getString("partyId")),
										EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS,
												"RESPONSIBLE_FOR"),
										EntityUtil.getFilterByDateExpr()), EntityOperator.AND));
							}
						}
						/** virtual team [end] */

						EntityCondition securityCondition = EntityCondition
								.makeCondition(UtilMisc.toList(securityConditions), EntityOperator.OR);
						conditions.add(securityCondition);
					}
					EntityCondition searchTlConditions = EntityCondition.makeCondition(
							EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS,
									userLogin.getString("partyId")),
							EntityCondition.makeCondition("securityGroupId", EntityOperator.EQUALS, "VT_SG_TL"),
							EntityUtil.getFilterByDateExpr());
					List<GenericValue> tlRelationship = delegator.findList("PartyRelationship", searchTlConditions,
							null, null, null, false);
					if (UtilValidate.isEmpty(tlRelationship)) {
						if (UtilValidate.isEmpty(leadAssignedTo)) {
							if (!"admin".equalsIgnoreCase(userLogin.getString("userLoginId"))) {
								List<GenericValue> aoRoles = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId",
										userLogin.getString("partyId"), "roleTypeId", "DBS_CENTRAL"), null, false);
								Debug.log("aoRoles  => " + aoRoles.size());
								if (UtilValidate.isEmpty(aoRoles)) {
									EntityCondition condition = EntityCondition.makeCondition("leadAssignTo",
											EntityOperator.EQUALS, userLogin.getString("partyId"));
									conditions.add(condition);
									isTeamLead = false;
								}
							}

						}
					}
					Debug.log("lowerPositionPartyIds  ==> " + lowerPositionPartyIds);
				}
			}

			EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);

			List<GenericValue> parties = delegator.findList("LeadSummaryView", mainConditons, null, null, null, false);

			if (parties != null && parties.size() > 0) {
				List<String> partyIdList = EntityUtil.getFieldListFromEntityList(parties, "partyId", true);
				List<Map<String, Object>> extractList = FastList.newInstance();
				if (partyIdList.size() > 0) {
					for (String leadId : partyIdList) {
						Map<String, Object> exportRow = new LinkedHashMap<String, Object>();

						Debug.log("LeadID => " + leadId);

						GenericValue partySummaryDetailsView = delegator.findOne("PartySummaryDetailsView",
								UtilMisc.toMap("partyId", leadId), false);
						if (UtilValidate.isNotEmpty(partySummaryDetailsView)) {
							String primary_phone_country_code = "";
							String phone_number1 = "";
							String phone_number2_country_code = "";
							String phone_number2 = "";
							String address1 = "";
							String address2 = "";
							String cityId = "";
							String stateProvinceGeoId = "";
							String postalCode = "";
							String email_address = "";
							String phoneNumberOneDndStatus = "";
							String phoneNumberTwoDndStatus = "";
							String personResponsible = "";
							String personResponsibleAssignBy = "";
							int daysInQueue = 0;
							String leadstatus = "";
							String leadsubstatus = "";
							String lastCalledDate = "";
							String lastMeetingDate = "";
							String facilityWithOtherBank = "";
							String amountWithOtherBank = "";
							String leadAssignedCity = "";
							String rmNotes = "";
							String howDidTheCallGo = "";
							String reasonNote = "";
							String reasonId = "";
							String contactedMobileNumber = "";

							String firstName1 = "";
							String lastName1 = "";
							String dinNumber1 = "";
							String uniqueIDNumber1 = "";
							String designation1 = "";
							String phone1 = "";
							String email1 = "";

							String firstName2 = "";
							String lastName2 = "";
							String dinNumber2 = "";
							String uniqueIDNumber2 = "";
							String designation2 = "";
							String phone2 = "";
							String email2 = "";

							String firstName3 = "";
							String lastName3 = "";
							String dinNumber3 = "";
							String uniqueIDNumber3 = "";
							String designation3 = "";
							String phone3 = "";
							String email3 = "";

							String firstName4 = "";
							String lastName4 = "";
							String dinNumber4 = "";
							String uniqueIDNumber4 = "";
							String designation4 = "";
							String phone4 = "";
							String email4 = "";

							String firstName5 = "";
							String lastName5 = "";
							String dinNumber5 = "";
							String uniqueIDNumber5 = "";
							String designation5 = "";
							String phone5 = "";
							String email5 = "";

							String dataSourceDesc = null;
							String dataSource = UtilValidate
									.isNotEmpty(partySummaryDetailsView.getString("createSource"))
											? partySummaryDetailsView.getString("createSource")
											: partySummaryDetailsView.getString("source");

							if (UtilValidate.isNotEmpty(dataSource)) {
								GenericValue leadSource = EntityUtil
										.getFirst(delegator.findByAnd("PartyIdentificationType",
												UtilMisc.toMap("partyIdentificationTypeId", dataSource), null, false));
								if (UtilValidate.isNotEmpty(leadSource)) {
									dataSourceDesc = "(" + leadSource.getString("partyIdentificationTypeId") + ") "
											+ leadSource.getString("description");
								}
							}
							if (UtilValidate.isNotEmpty(source)) {
								if (!source.contains(dataSource))
									continue;
							}

							String leadAssignFromName = "";
							if (UtilValidate.isNotEmpty(partySummaryDetailsView.getString("leadAssignBy"))) {
								leadAssignFromName = PartyHelper.getPartyName(delegator,
										partySummaryDetailsView.getString("leadAssignBy"), false);
							}

							/** Displaying lead assigned from id , if leadAssignBy is empty */
							if (UtilValidate.isEmpty(leadAssignFromName)) {
								GenericValue isOneBankIdExists = delegator
										.findOne("UserLogin",
												UtilMisc.toMap("userLoginId",
														partySummaryDetailsView.getString("uploadedByUserLoginId")),
												true);
								leadAssignFromName = PartyHelper.getPartyName(delegator,
										isOneBankIdExists.getString("partyId"), false);
							}

							String leadAssignToName = "";
							String managerName = "";
							String managerId = "";
							if (UtilValidate.isNotEmpty(partySummaryDetailsView.getString("leadAssignTo"))) {
								if (!"admin".equalsIgnoreCase(userLogin.getString("userLoginId")) && isTeamLead) {
									List<GenericValue> aoRoles = delegator.findByAnd("PartyRole",
											UtilMisc.toMap("partyId", userLogin.getString("partyId"), "roleTypeId",
													"DBS_CENTRAL"),
											null, false);
									if (UtilValidate.isEmpty(aoRoles)) {
										if (!multiTeamLists
												.contains(partySummaryDetailsView.getString("leadAssignTo"))) {
											continue;
										}
									}
								}
								leadAssignToName = PartyHelper.getPartyName(delegator,
										partySummaryDetailsView.getString("leadAssignTo"), false);
								GenericValue partyIdFrom = EntityUtil.getFirst(delegator.findByAnd("PartyRelationship",
										UtilMisc.toMap("partyIdTo", partySummaryDetailsView.getString("leadAssignTo"),
												"roleTypeIdFrom", "ACCOUNT_TEAM"),
										UtilMisc.toList("fromDate DESC"), false));
								if (UtilValidate.isNotEmpty(partyIdFrom)) {
									Map<String, Object> vTeamMemberList = VirtualTeamUtil.getVirtualTeamLead(delegator,
											partyIdFrom.getString("partyIdFrom"));
									if (UtilValidate.isNotEmpty(vTeamMemberList)) {
										managerId = (String) vTeamMemberList.get("virtualTeamMemberId");
										managerName = (String) vTeamMemberList.get("virtualTeamMemberName");
									}
								}
							}
							if (UtilValidate.isNotEmpty(userManager)) {
								if (!userManager.contains(managerId)) {
									continue;
								}
							}

							/** Get Lead Status from LeadStatus Table */
							String leadStatusId = "";
							GenericValue leadStatusList = EntityUtil.getFirst(
									delegator.findByAnd("LeadStatus", UtilMisc.toMap("leadId", leadId), null, false));
							if (UtilValidate.isNotEmpty(leadStatusList)) {
								if (UtilValidate.isNotEmpty(leadStatusList.getString("statusId"))) {
									GenericValue enumeration = EntityUtil.getFirst(delegator.findByAnd("Enumeration",
											UtilMisc.toMap("enumCode", leadStatusList.getString("statusId")), null,
											false));
									if (UtilValidate.isNotEmpty(enumeration)) {
										leadStatusId = leadStatusList.getString("statusId");
										leadstatus = enumeration.getString("description");
									}
									if (leadStatusId.contains("DROPPED")) {
										reasonId = leadStatusList.getString("reasonId");
										reasonNote = leadStatusList.getString("reasonNote");
									}
								}
							}

							/** Get Lead Sub Status from WorkEffortLog Table and Filter process */
							String leadSubStatusId = "";
							GenericValue leadSubStatusList = EntityUtil
									.getFirst(delegator.findByAnd("WorkEffortLog", UtilMisc.toMap("companyId", leadId),
											UtilMisc.toList("lastUpdatedStamp DESC"), false));
							if (UtilValidate.isNotEmpty(leadSubStatusList)) {
								if (UtilValidate.isNotEmpty(leadSubStatusList.getString("outcomeId"))) {
									GenericValue enumeration = EntityUtil.getFirst(delegator.findByAnd("Enumeration",
											UtilMisc.toMap("enumId", leadSubStatusList.getString("outcomeId")), null,
											false));
									if (UtilValidate.isNotEmpty(enumeration)) {
										leadSubStatusId = leadSubStatusList.getString("outcomeId");
										leadsubstatus = enumeration.getString("description");
									}
								}
							}

							if (UtilValidate.isNotEmpty(leadSubStatus)) {
								if (!leadSubStatus.contains(leadSubStatusId))
									continue;
							}

							if (UtilValidate
									.isNotEmpty(partySummaryDetailsView.getString("keyContactPerson1PartyId"))) {
								String personId1 = partySummaryDetailsView.getString("keyContactPerson1PartyId");
								GenericValue person1List = EntityUtil.getFirst(delegator.findByAnd("Person",
										UtilMisc.toMap("partyId", personId1), null, false));
								if (UtilValidate.isNotEmpty(person1List)) {
									if (UtilValidate.isNotEmpty(person1List.getString("designation"))) {
										GenericValue enumeration = EntityUtil
												.getFirst(delegator.findByAnd("Enumeration",
														UtilMisc.toMap("enumId", person1List.getString("designation")),
														null, false));
										if (UtilValidate.isNotEmpty(enumeration)) {
											designation1 = enumeration.getString("description");
										}
									}
									firstName1 = person1List.getString("firstName");
									lastName1 = person1List.getString("lastName");
									dinNumber1 = person1List.getString("dinNumber");
									uniqueIDNumber1 = person1List.getString("uniqueIDNumber");
								}
								phone1 = getPhone(delegator, personId1);
								email1 = getEmail(delegator, personId1);
								Debug.log("Phone1 and personId1 => " + phone1 + " - " + personId1);
							}
							if (UtilValidate
									.isNotEmpty(partySummaryDetailsView.getString("keyContactPerson2PartyId"))) {
								String personId2 = partySummaryDetailsView.getString("keyContactPerson2PartyId");
								GenericValue person2List = EntityUtil.getFirst(delegator.findByAnd("Person",
										UtilMisc.toMap("partyId", personId2), null, false));
								if (UtilValidate.isNotEmpty(person2List)) {
									if (UtilValidate.isNotEmpty(person2List.getString("designation"))) {
										GenericValue enumeration = EntityUtil
												.getFirst(delegator.findByAnd("Enumeration",
														UtilMisc.toMap("enumId", person2List.getString("designation")),
														null, false));
										if (UtilValidate.isNotEmpty(enumeration)) {
											designation2 = enumeration.getString("description");
										}
									}
									firstName2 = person2List.getString("firstName");
									lastName2 = person2List.getString("lastName");
									dinNumber2 = person2List.getString("dinNumber");
									uniqueIDNumber2 = person2List.getString("uniqueIDNumber");
								}
								phone2 = getPhone(delegator, personId2);
								email2 = getEmail(delegator, personId2);
							}
							if (UtilValidate
									.isNotEmpty(partySummaryDetailsView.getString("keyContactPerson3PartyId"))) {
								String personId3 = partySummaryDetailsView.getString("keyContactPerson3PartyId");
								GenericValue person3List = EntityUtil.getFirst(delegator.findByAnd("Person",
										UtilMisc.toMap("partyId", personId3), null, false));
								if (UtilValidate.isNotEmpty(person3List)) {
									if (UtilValidate.isNotEmpty(person3List.getString("designation"))) {
										GenericValue enumeration = EntityUtil
												.getFirst(delegator.findByAnd("Enumeration",
														UtilMisc.toMap("enumId", person3List.getString("designation")),
														null, false));
										if (UtilValidate.isNotEmpty(enumeration)) {
											designation3 = enumeration.getString("description");
										}
									}
									firstName3 = person3List.getString("firstName");
									lastName3 = person3List.getString("lastName");
									dinNumber3 = person3List.getString("dinNumber");
									uniqueIDNumber3 = person3List.getString("uniqueIDNumber");
								}
								phone3 = getPhone(delegator, personId3);
								email3 = getEmail(delegator, personId3);
							}
							if (UtilValidate
									.isNotEmpty(partySummaryDetailsView.getString("keyContactPerson4PartyId"))) {
								String personId4 = partySummaryDetailsView.getString("keyContactPerson4PartyId");
								GenericValue person4List = EntityUtil.getFirst(delegator.findByAnd("Person",
										UtilMisc.toMap("partyId", personId4), null, false));
								if (UtilValidate.isNotEmpty(person4List)) {
									if (UtilValidate.isNotEmpty(person4List.getString("designation"))) {
										GenericValue enumeration = EntityUtil
												.getFirst(delegator.findByAnd("Enumeration",
														UtilMisc.toMap("enumId", person4List.getString("designation")),
														null, false));
										if (UtilValidate.isNotEmpty(enumeration)) {
											designation4 = enumeration.getString("description");
										}
									}
									firstName4 = person4List.getString("firstName");
									lastName4 = person4List.getString("lastName");
									dinNumber4 = person4List.getString("dinNumber");
									uniqueIDNumber4 = person4List.getString("uniqueIDNumber");
								}
								phone4 = getPhone(delegator, personId4);
								email4 = getEmail(delegator, personId4);
							}
							if (UtilValidate
									.isNotEmpty(partySummaryDetailsView.getString("keyContactPerson5PartyId"))) {
								String personId5 = partySummaryDetailsView.getString("keyContactPerson5PartyId");
								GenericValue person5List = EntityUtil.getFirst(delegator.findByAnd("Person",
										UtilMisc.toMap("partyId", personId5), null, false));
								if (UtilValidate.isNotEmpty(person5List)) {
									if (UtilValidate.isNotEmpty(person5List.getString("designation"))) {
										GenericValue enumeration = EntityUtil
												.getFirst(delegator.findByAnd("Enumeration",
														UtilMisc.toMap("enumId", person5List.getString("designation")),
														null, false));
										if (UtilValidate.isNotEmpty(enumeration)) {
											designation5 = enumeration.getString("description");
										}
									}
									firstName5 = person5List.getString("firstName");
									lastName5 = person5List.getString("lastName");
									dinNumber5 = person5List.getString("dinNumber");
									uniqueIDNumber5 = person5List.getString("uniqueIDNumber");
								}
								phone5 = getPhone(delegator, personId5);
								email5 = getEmail(delegator, personId5);
							}

							List<GenericValue> partyContactMechs = delegator.findByAnd("PartyContactMech",
									UtilMisc.toMap("partyId", leadId), null, false);
							if (partyContactMechs != null && partyContactMechs.size() > 0) {
								partyContactMechs = EntityUtil.filterByDate(partyContactMechs);
								if (partyContactMechs != null && partyContactMechs.size() > 0) {
									partyContactMechs = EntityUtil.getFieldListFromEntityList(partyContactMechs,
											"contactMechId", true);
								}
								if (partyContactMechs != null && partyContactMechs.size() > 0) {
									Set<String> findOptions = UtilMisc.toSet("contactMechId");
									List<String> orderBy = UtilMisc.toList("createdStamp DESC");

									EntityCondition condition1 = EntityCondition.makeCondition("partyId",
											EntityOperator.EQUALS, leadId);
									EntityCondition condition2 = EntityCondition.makeCondition("contactMechId",
											EntityOperator.IN, partyContactMechs);

									EntityCondition primaryPhoneConditions = EntityCondition
											.makeCondition(UtilMisc.toList(condition1, condition2,
													EntityCondition.makeCondition("contactMechPurposeTypeId",
															EntityOperator.EQUALS, "PRIMARY_PHONE")));
									List<GenericValue> primaryPhones = delegator.findList("PartyContactMechPurpose",
											primaryPhoneConditions, findOptions, null, null, false);
									if (primaryPhones != null && primaryPhones.size() > 0) {
										GenericValue primaryPhone = EntityUtil
												.getFirst(EntityUtil.filterByDate(primaryPhones));
										if (UtilValidate.isNotEmpty(primaryPhone)) {
											GenericValue primaryPhoneNumber = delegator.findOne("TelecomNumber",
													UtilMisc.toMap("contactMechId",
															primaryPhone.getString("contactMechId")),
													false);
											if (UtilValidate.isNotEmpty(primaryPhoneNumber)) {
												phone_number1 = primaryPhoneNumber.getString("contactNumber");
												primary_phone_country_code = primaryPhoneNumber
														.getString("countryCode");
												phoneNumberOneDndStatus = primaryPhoneNumber.getString("dndStatus");
											}
										}
									}

									EntityCondition secondaryPhoneConditions = EntityCondition
											.makeCondition(UtilMisc.toList(condition1, condition2,
													EntityCondition.makeCondition("contactMechPurposeTypeId",
															EntityOperator.EQUALS, "PHONE_WORK_SEC")));
									List<GenericValue> secondaryPhones = delegator.findList("PartyContactMechPurpose",
											secondaryPhoneConditions, findOptions, null, null, false);
									if (secondaryPhones != null && secondaryPhones.size() > 0) {
										GenericValue secondaryPhone = EntityUtil
												.getFirst(EntityUtil.filterByDate(secondaryPhones));
										if (UtilValidate.isNotEmpty(secondaryPhone)) {
											GenericValue secondaryPhoneNumber = delegator.findOne("TelecomNumber",
													UtilMisc.toMap("contactMechId",
															secondaryPhone.getString("contactMechId")),
													false);
											if (UtilValidate.isNotEmpty(secondaryPhoneNumber)) {
												phone_number2 = secondaryPhoneNumber.getString("contactNumber");
												phone_number2_country_code = secondaryPhoneNumber
														.getString("countryCode");
												phoneNumberTwoDndStatus = secondaryPhoneNumber.getString("dndStatus");
											}
										}
									}

									EntityCondition primaryEmailConditions = EntityCondition
											.makeCondition(UtilMisc.toList(condition1, condition2,
													EntityCondition.makeCondition("contactMechPurposeTypeId",
															EntityOperator.EQUALS, "PRIMARY_EMAIL")));
									List<GenericValue> primaryEmails = delegator.findList("PartyContactMechPurpose",
											primaryEmailConditions, findOptions, null, null, false);
									if (primaryEmails != null && primaryEmails.size() > 0) {
										GenericValue primaryEmail = EntityUtil
												.getFirst(EntityUtil.filterByDate(primaryEmails));
										if (UtilValidate.isNotEmpty(primaryEmail)) {
											GenericValue primaryInfoString = delegator.findOne("ContactMech", UtilMisc
													.toMap("contactMechId", primaryEmail.getString("contactMechId")),
													false);
											if (UtilValidate.isNotEmpty(primaryInfoString)) {
												email_address = primaryInfoString.getString("infoString");
											}
										}
									}

									EntityCondition postalAddressConditions = EntityCondition
											.makeCondition(UtilMisc.toList(condition1, condition2,
													EntityCondition.makeCondition("contactMechPurposeTypeId",
															EntityOperator.EQUALS, "PRIMARY_LOCATION")));
									List<GenericValue> primaryAddressList = delegator.findList(
											"PartyContactMechPurpose", postalAddressConditions, findOptions, null,
											null, false);
									if (primaryAddressList != null && primaryAddressList.size() > 0) {
										GenericValue primaryAddress = EntityUtil
												.getFirst(EntityUtil.filterByDate(primaryAddressList));
										if (UtilValidate.isNotEmpty(primaryAddress)) {
											GenericValue postalAddress = delegator.findOne("PostalAddress", UtilMisc
													.toMap("contactMechId", primaryAddress.getString("contactMechId")),
													false);
											if (UtilValidate.isNotEmpty(postalAddress)) {
												cityId = postalAddress.getString("city");
												stateProvinceGeoId = postalAddress.getString("stateProvinceGeoId");
												address1 = postalAddress.getString("address1");
												address2 = postalAddress.getString("address2");
												postalCode = postalAddress.getString("postalCode");
											}
										}
									}
								}
							}

							/** Get Company Cin, GSTN and IEC Code */
							Map<String, String> partyAttrib = getPartyAttrs(delegator, leadId);
							EntityCondition conditionPR = EntityCondition.makeCondition(UtilMisc.toList(
									EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, leadId),
									EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS,
											"ACCOUNT_MANAGER"),
									EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN,
											UtilMisc.toList("ACCOUNT", "LEAD", "CONTACT")),
									EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS,
											"RESPONSIBLE_FOR"),
									EntityUtil.getFilterByDateExpr()), EntityOperator.AND);

							GenericValue responsibleFor = EntityQuery.use(delegator).from("PartyRelationship")
									.where(conditionPR).orderBy("fromDate DESC").queryFirst();

							if (UtilValidate.isNotEmpty(responsibleFor)) {

								if (UtilValidate.isNotEmpty(responsibleFor.getString("partyIdTo"))) {
									personResponsible = PartyHelper.getPartyName(delegator,
											responsibleFor.getString("partyIdTo"), false);
								}

								if (UtilValidate.isNotEmpty(responsibleFor.getString("createdByUserLoginId"))) {
									GenericValue createdByUserLogin = EntityQuery.use(delegator).from("UserLogin")
											.where("userLoginId", responsibleFor.getString("createdByUserLoginId"))
											.queryFirst();
									if (UtilValidate.isNotEmpty(createdByUserLogin.getString("partyId"))) {
										personResponsibleAssignBy = PartyHelper.getPartyName(delegator,
												createdByUserLogin.getString("partyId"), false);
									}
								}
								if (UtilValidate.isNotEmpty(responsibleFor.getTimestamp("fromDate"))) {
									daysInQueue = UtilDateTime.getIntervalInDays(
											responsibleFor.getTimestamp("fromDate"), UtilDateTime.nowTimestamp());
								}
							}

							/** Start Get data for No of Attempts */
							List<EntityCondition> attemptConditions = new ArrayList<EntityCondition>();
							attemptConditions.add(EntityCondition.makeCondition(EntityOperator.AND,
									EntityCondition.makeCondition("leadId", EntityOperator.EQUALS, leadId),
									EntityCondition.makeCondition("type", EntityOperator.EQUALS, "LMS_CALL")));

							long callAttempts = delegator.findCountByCondition("LeadCallHistory",
									EntityCondition.makeCondition(attemptConditions), null, null);

							List<EntityCondition> conditionsMeet = new ArrayList<EntityCondition>();
							conditionsMeet.add(EntityCondition.makeCondition(EntityOperator.AND,
									EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, leadId),
									EntityCondition.makeCondition("workEffortTypeId", EntityOperator.EQUALS,
											"LMS_MEETING"),
									EntityCondition.makeCondition("estimatedCompletionDate", EntityOperator.LESS_THAN,
											UtilDateTime.nowTimestamp()),
									EntityCondition.makeCondition("currentStatusId", EntityOperator.NOT_EQUAL,
											"EVENT_CANCELLED")));

							long meetingCount = delegator.findCountByCondition("WorkEffortAndPartyAssign",
									EntityCondition.makeCondition(conditionsMeet), null, null);

							callAttempts = meetingCount + callAttempts;

							/** End Get data for No of Attempts */

							/** Get Last Called Date */
							GenericValue workEffortCall = EntityUtil.getFirst(delegator.findByAnd("WorkEffort",
									UtilMisc.toMap("workEffortName", partySummaryDetailsView.getString("companyName"),
											"workEffortTypeId", "LMS_CALL"),
									UtilMisc.toList("estimatedCompletionDate DESC"), false));
							if (UtilValidate.isNotEmpty(workEffortCall)) {
								lastCalledDate = workEffortCall.getString("estimatedCompletionDate");
							}

							/** Get Last Meeting Date */
							GenericValue workEffortMeeting = EntityUtil.getFirst(delegator.findByAnd("WorkEffort",
									UtilMisc.toMap("workEffortName", partySummaryDetailsView.getString("companyName"),
											"workEffortTypeId", "LMS_MEETING"),
									UtilMisc.toList("estimatedCompletionDate DESC"), false));
							if (UtilValidate.isNotEmpty(workEffortMeeting)) {
								lastMeetingDate = workEffortMeeting.getString("estimatedCompletionDate");
							}

							String city = "";
							String state = "";
							EntityCondition postalAddressConditions = EntityCondition
									.makeCondition(
											UtilMisc.toList(
													EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,
															leadId),
													EntityCondition.makeCondition("contactMechPurposeTypeId",
															EntityOperator.EQUALS, "PRIMARY_LOCATION")),
											EntityOperator.AND);

							GenericValue primaryAddress = EntityUtil.getFirst(delegator.findList(
									"PartyContactMechPurpose", postalAddressConditions, UtilMisc.toSet("contactMechId"),
									null, null, false));
							if (UtilValidate.isNotEmpty(primaryAddress)) {
								GenericValue postalAddress = delegator.findOne("PostalAddress",
										UtilMisc.toMap("contactMechId", primaryAddress.getString("contactMechId")),
										false);
								if (UtilValidate.isNotEmpty(postalAddress)) {
									city = postalAddress.getString("city");
									if (UtilValidate.isNotEmpty(city)) {
										GenericValue geo = delegator.findOne("Geo", UtilMisc.toMap("geoId", city),
												false);
										if (UtilValidate.isNotEmpty(geo)) {
											leadAssignedCity = geo.getString("geoName");
										}
									}
								}
							}

							GenericValue workEffortLead = EntityQuery.use(delegator).from("WorkEffortPartyAssignment")
									.where("partyId", leadId, "roleTypeId", "LEAD").orderBy("lastUpdatedStamp DESC")
									.queryFirst();
							if (UtilValidate.isNotEmpty(workEffortLead)) {
								if (UtilValidate.isNotEmpty(workEffortLead.getString("workEffortId"))) {
									GenericValue workEffortLogLead = EntityQuery.use(delegator).from("WorkEffortLog")
											.where("workEffortId", workEffortLead.getString("workEffortId"))
											.orderBy("lastUpdatedStamp DESC").queryFirst();
									if (UtilValidate.isNotEmpty(workEffortLogLead)) {
										if (UtilValidate.isNotEmpty(workEffortLogLead.getString("outcomeId"))) {
											GenericValue enumeration = EntityQuery.use(delegator).from("Enumeration")
													.where("enumId", workEffortLogLead.getString("outcomeId"))
													.queryOne();
											if (UtilValidate.isNotEmpty(enumeration.getString("description"))) {
												howDidTheCallGo = enumeration.getString("description");
											}
										}
									}
									GenericValue workEffortNoteLead = EntityQuery.use(delegator).from("WorkEffortNote")
											.where("workEffortId", workEffortLead.getString("workEffortId"))
											.orderBy("lastUpdatedStamp DESC").queryFirst();
									if (UtilValidate.isNotEmpty(workEffortNoteLead)) {
										if (UtilValidate.isNotEmpty(workEffortNoteLead.getString("noteId"))) {
											GenericValue noteData = EntityQuery.use(delegator).from("NoteData")
													.where("noteId", workEffortNoteLead.getString("noteId")).queryOne();
											if (UtilValidate.isNotEmpty(noteData)) {
												rmNotes = noteData.getString("noteInfo");
											}
										}
									}
								}
							}

							/** Get Contacted Mobile Number */
							GenericValue workEffortLogData = EntityUtil.getFirst(delegator.findByAnd("WorkEffortLog",
									UtilMisc.toMap("companyId", leadId), null, false));

							if (UtilValidate.isNotEmpty(workEffortLogData)) {
								EntityCondition contMobNum = EntityCondition.makeCondition(
										EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS,
												workEffortLogData.get("workEffortId")),
										EntityCondition.makeCondition("roleTypeId", EntityOperator.IN,
												UtilMisc.toList("CONTACT", "MAIN_LINE_1")));

								GenericValue contMobNumData = EntityQuery.use(delegator)
										.from("WorkEffortPartyAssignment").where(contMobNum)
										.orderBy("createdStamp DESC").queryFirst();
								if (UtilValidate.isNotEmpty(contMobNumData)) {
									GenericValue contactMechId = EntityQuery.use(delegator)
											.from("WorkEffortContactMech")
											.where("workEffortId", contMobNumData.getString("workEffortId")).queryOne();
									if (UtilValidate.isNotEmpty(contactMechId)) {
										GenericValue contactedNumber = EntityUtil
												.getFirst(delegator.findByAnd("TelecomNumber",
														UtilMisc.toMap("contactMechId",
																contactMechId.getString("contactMechId")),
														null, false));
										if (UtilValidate.isNotEmpty(contactedNumber))
											contactedMobileNumber = contactedNumber.getString("contactNumber");
									}
								}
							}

							/** Data Export process in CSV/EXCEL start */
							exportRow.put("Lead Id", leadId);

							ExportWrapper.getData(selectedFields, "Company Name",
									partySummaryDetailsView.getString("companyName") != null
											? partySummaryDetailsView.getString("companyName")
											: "",
									exportRow);

							ExportWrapper.getData(selectedFields, "Company CIN", partyAttrib.get("cin"), exportRow);

							ExportWrapper.getData(selectedFields, "Date of Incorporation",
									partySummaryDetailsView.getString("dateOfIncorporation") != null
											? partySummaryDetailsView.getString("dateOfIncorporation")
											: "",
									exportRow);

							ExportWrapper.getData(selectedFields, "Industry", DataHelper.getEnumDescription(delegator,
									partySummaryDetailsView.getString("industry"), "DBS_INDUSTRY"), exportRow);

							ExportWrapper.getData(selectedFields, "Constitution",
									DataHelper.getEnumDescription(delegator,
											partySummaryDetailsView.getString("constitution"), "DBS_CONSTITUTION"),
									exportRow);

							ExportWrapper.getData(selectedFields, "Sales Turnover",
									partySummaryDetailsView.getString("salesTurnover") != null
											? partySummaryDetailsView.getString("salesTurnover")
											: "",
									exportRow);

							ExportWrapper.getData(selectedFields, "No of Employees",
									partySummaryDetailsView.getString("noOfEmployees") != null
											? partySummaryDetailsView.getString("noOfEmployees")
											: "",
									exportRow);

							ExportWrapper.getData(selectedFields, "TCP User",
									DataHelper.getEnumDescription(delegator,
											partySummaryDetailsView.getString("tallyUserType"), "DBS_TALLY_USR_TYPE"),
									exportRow);

							ExportWrapper.getData(selectedFields, "Source of Lead", dataSourceDesc, exportRow);

							ExportWrapper
									.getData(selectedFields, "Intermediary Partner's Name",
											DataHelper.getEnumDescription(delegator,
													partySummaryDetailsView.getString("tcpName"), "DBS_TCP_NAME"),
											exportRow);

							ExportWrapper.getData(selectedFields, "First Name of Contact Person 1", firstName1,
									exportRow);
							ExportWrapper.getData(selectedFields, "Last Name of Contact Person 1", lastName1,
									exportRow);
							ExportWrapper.getData(selectedFields, "DIN of Contact Person 1", dinNumber1, exportRow);
							ExportWrapper.getData(selectedFields, "Unique Identification of Contact Person 1",
									uniqueIDNumber1, exportRow);
							ExportWrapper.getData(selectedFields, "Email Address of Contact Person 1", email1,
									exportRow);
							ExportWrapper.getData(selectedFields, "Designation of Contact Person 1", designation1,
									exportRow);
							ExportWrapper.getData(selectedFields, "Mobile of Contact Person 1", phone1, exportRow);

							ExportWrapper.getData(selectedFields, "Office Main Line", phone_number1, exportRow);
							ExportWrapper.getData(selectedFields, "Street Name", address1, exportRow);

							ExportWrapper.getData(selectedFields, "PIN Code", postalCode, exportRow);

							ExportWrapper.getData(selectedFields, "City",
									DataHelper.getGeoName(delegator, partySummaryDetailsView.getString("city"), "CITY"),
									exportRow);

							ExportWrapper.getData(selectedFields, "State",
									DataHelper.getGeoName(delegator,
											partySummaryDetailsView.getString("stateProvinceGeoId"), "STATE"),
									exportRow);

							ExportWrapper.getData(selectedFields, "Lead Assigned To", personResponsible, exportRow);

							ExportWrapper.getData(selectedFields, "Lead Assigned From", personResponsibleAssignBy,
									exportRow);

							ExportWrapper.getData(selectedFields, "Company UEN/PAN",
									partySummaryDetailsView.getString("permanentAcccountNumber") != null
											? partySummaryDetailsView.getString("permanentAcccountNumber")
											: "",
									exportRow);

							ExportWrapper.getData(selectedFields, "Other Banks",
									DataHelper.getEnumDescription(delegator,
											partySummaryDetailsView.getString("otherBankName"), "DBS_EXISTING_BN"),
									exportRow);

							ExportWrapper.getData(selectedFields, "Facilities with Other Banks",
									DataHelper.getEnumDescription(delegator,
											partySummaryDetailsView.getString("facilityWithOtherBank"), "DBS_LMS_PROD"),
									exportRow);

							ExportWrapper.getData(selectedFields, "Amount with Other banks",
									partySummaryDetailsView.getString("amountWithOtherBank") != null
											? partySummaryDetailsView.getString("amountWithOtherBank")
											: "",
									exportRow);

							ExportWrapper.getData(selectedFields, "Paid-Up Capital",
									partySummaryDetailsView.getString("paidupCapital") != null
											? partySummaryDetailsView.getString("paidupCapital")
											: "",
									exportRow);

							ExportWrapper.getData(selectedFields, "GSTN", partyAttrib.get("gstn"), exportRow);
							ExportWrapper.getData(selectedFields, "IEC Code", partyAttrib.get("iecCode"), exportRow);
							ExportWrapper.getData(selectedFields, "Preferred Language",
									partySummaryDetailsView.getString("preferredLanguages") != null
											? partySummaryDetailsView.getString("preferredLanguages")
											: "",
									exportRow);

							ExportWrapper.getData(selectedFields, "Interested Product Name",
									DataHelper.getEnumDescription(delegator,
											partySummaryDetailsView.getString("interestedProductName"), "DBS_LMS_PROD"),
									exportRow);

							ExportWrapper.getData(selectedFields, "Product - Amount",
									partySummaryDetailsView.getString("productAmount") != null
											? partySummaryDetailsView.getString("productAmount")
											: "",
									exportRow);

							ExportWrapper
									.getData(selectedFields, "Lead Classification",
											DataHelper.getEnumDescription(delegator,
													partySummaryDetailsView.getString("leadScore"), "LEAD_SCORE"),
											exportRow);

							ExportWrapper.getData(selectedFields, "How did the call go?", howDidTheCallGo, exportRow);
							ExportWrapper.getData(selectedFields, "Reason of lead drop",
									DataHelper.getEnumDescription(delegator, reasonId, "DROPPED_LEAD"), exportRow);
							ExportWrapper.getData(selectedFields, "RM call notes", rmNotes, exportRow);
							ExportWrapper.getData(selectedFields, "Reason of lead drop  - text field", reasonNote,
									exportRow);

							ExportWrapper.getData(selectedFields, "Lead Status", leadstatus, exportRow);

							ExportWrapper.getData(selectedFields, "Lead Sub Status", leadsubstatus, exportRow);

							ExportWrapper.getData(selectedFields, "Number of attempts", String.valueOf(callAttempts),
									exportRow);
							ExportWrapper.getData(selectedFields, "Last called Date", lastCalledDate, exportRow);
							ExportWrapper.getData(selectedFields, "Manager Name", managerName, exportRow);
							ExportWrapper.getData(selectedFields, "Last Meeting Date", lastMeetingDate, exportRow);
							ExportWrapper.getData(selectedFields, "Lead Assigned to City", leadAssignedCity, exportRow);
							ExportWrapper.getData(selectedFields, "Days in queue", String.valueOf(daysInQueue),
									exportRow);

							ExportWrapper.getData(selectedFields, "First Name of Contact Person 2", firstName2,
									exportRow);
							ExportWrapper.getData(selectedFields, "Last Name of Contact Person 2", lastName2,
									exportRow);
							ExportWrapper.getData(selectedFields, "DIN of Contact Person 2", dinNumber2, exportRow);
							ExportWrapper.getData(selectedFields, "Unique Identification of Contact Person 2",
									uniqueIDNumber2, exportRow);
							ExportWrapper.getData(selectedFields, "Email Address of Contact Person 2", email2,
									exportRow);
							ExportWrapper.getData(selectedFields, "Designation of Contact Person 2", designation2,
									exportRow);
							ExportWrapper.getData(selectedFields, "Mobile of Contact Person 2", phone2, exportRow);

							ExportWrapper.getData(selectedFields, "First Name of Contact Person 3", firstName3,
									exportRow);
							ExportWrapper.getData(selectedFields, "Last Name of Contact Person 3", lastName3,
									exportRow);
							ExportWrapper.getData(selectedFields, "DIN of Contact Person 3", dinNumber3, exportRow);
							ExportWrapper.getData(selectedFields, "Unique Identification of Contact Person 3",
									uniqueIDNumber3, exportRow);
							ExportWrapper.getData(selectedFields, "Email Address of Contact Person 3", email3,
									exportRow);
							ExportWrapper.getData(selectedFields, "Designation of Contact Person 3", designation3,
									exportRow);
							ExportWrapper.getData(selectedFields, "Mobile of Contact Person 3", phone3, exportRow);

							ExportWrapper.getData(selectedFields, "First Name of Contact Person 4", firstName4,
									exportRow);
							ExportWrapper.getData(selectedFields, "Last Name of Contact Person 4", lastName4,
									exportRow);
							ExportWrapper.getData(selectedFields, "DIN of Contact Person 4", dinNumber4, exportRow);
							ExportWrapper.getData(selectedFields, "Unique Identification of Contact Person 4",
									uniqueIDNumber4, exportRow);
							ExportWrapper.getData(selectedFields, "Email Address of Contact Person 4", email4,
									exportRow);
							ExportWrapper.getData(selectedFields, "Designation of Contact Person 4", designation4,
									exportRow);
							ExportWrapper.getData(selectedFields, "Mobile of Contact Person 4", phone4, exportRow);

							ExportWrapper.getData(selectedFields, "First Name of Contact Person 5", firstName5,
									exportRow);
							ExportWrapper.getData(selectedFields, "Last Name of Contact Person 5", lastName5,
									exportRow);
							ExportWrapper.getData(selectedFields, "DIN of Contact Person 5", dinNumber5, exportRow);
							ExportWrapper.getData(selectedFields, "Unique Identification of Contact Person 5",
									uniqueIDNumber5, exportRow);
							ExportWrapper.getData(selectedFields, "Email Address of Contact Person 5", email5,
									exportRow);
							ExportWrapper.getData(selectedFields, "Designation of Contact Person 5", designation5,
									exportRow);
							ExportWrapper.getData(selectedFields, "Mobile of Contact Person 5", phone5, exportRow);
							ExportWrapper.getData(selectedFields, "Contacted Mobile Number", contactedMobileNumber,
									exportRow);

							extractList.add(exportRow);

						}
					}
				}

				if (extractList.size() > 0)

				{

					String fileName = "LeadExport_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
							+ ExportUtil.getFileExtension(exportType);
					String templateLocation = ComponentConfig.getRootLocation("crm") + "/webapp/crm-resource/template";
					String delimiter = ",";

					Map<String, Object> exportContext = new HashMap<String, Object>();

					exportContext.put("delegator", delegator);
					exportContext.put("rows", extractList);
					exportContext.put("headers", null);
					exportContext.put("fileName", fileName);
					exportContext.put("location", templateLocation);
					exportContext.put("delimiter", delimiter);
					exportContext.put("isHeaderRequird", true);

					exportContext.put("exportType", exportType);

					ExporterFacade.exportReport(exportContext);

					Thread.sleep(1000);

					String filePath = templateLocation + File.separatorChar + fileName;

					ExporterFacade.downloadReport(request, response, filePath, exportType);

					boolean isdelete = true;
					if (isdelete) {
						File file = new File(filePath);
						file.delete();
					}
				}
			} else

			{
				request.setAttribute("_ERROR_MESSAGE_", "No Records found to Export");
				returnMessage = "error";
			}
		} catch (

		Exception e) {
			Debug.logError("Error : " + e.getMessage(), module);
			return "error";
		}
		return returnMessage;
	}

	@SuppressWarnings("unchecked")
	public static String exportLeadErrorLog(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {

		HttpSession session = request.getSession();
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		String exportType = request.getParameter("exportType");

		List<String> selectedFields = UtilCommon.getArrayToList(request.getParameter("selectedFields"));
		Debug.log("==selectedFields  ===" + selectedFields);
		try {
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();
			List<String> partyIdsTo = new LinkedList<String>();

			String importStatusId = request.getParameter("importStatusId");
			String partyId = request.getParameter("leadId");
			String batchId = request.getParameter("batchId");
			String firstName = request.getParameter("firstName");
			String lastName = request.getParameter("lastName");
			String uploadedByUserLoginId = request.getParameter("uploadedByUserLoginId");
			String errorCodeId = request.getParameter("errorCodeId");

			String draw = request.getParameter("draw");
			String start = request.getParameter("start");
			String length = request.getParameter("length");

			String sortDir = "desc";
			String orderField = "";
			String orderColumnId = request.getParameter("order[0][column]");
			if (UtilValidate.isNotEmpty(orderColumnId)) {
				int sortColumnId = Integer.parseInt(orderColumnId);
				String sortColumnName = request.getParameter("columns[" + sortColumnId + "][data]");
				sortDir = request.getParameter("order[0][dir]").toUpperCase();
				orderField = sortColumnName;
			} else {
				orderField = "createdStamp";
			}

			boolean isApprover = false;
			/*
			 * security = request.getAttribute("security"); if
			 * (security.hasPermission("ETL-IMPDAT-APPROVER", userLogin)) { isApprover =
			 * true; }
			 */

			if (UtilValidate.isNotEmpty(importStatusId)) {
				if (importStatusId.equals("DISABLED")) {
					conditions.add(
							EntityCondition.makeCondition("partyStatusId", EntityOperator.EQUALS, "PARTY_DISABLED"));
				} else if (importStatusId.equals("DATAIMP_ERROR")) {

					EntityCondition statusCondition = EntityCondition.makeCondition(UtilMisc.toList(
							EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_ERROR"),
							EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_FAILED")),
							EntityOperator.OR);
					conditions.add(statusCondition);
				} else {
					EntityCondition statusCondition = EntityCondition.makeCondition(UtilMisc.toList(
							EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, importStatusId)),
							EntityOperator.AND);
					conditions.add(statusCondition);
				}
			} else {
				/*
				 * EntityCondition statusCondition = EntityCondition.makeCondition([
				 * EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS,
				 * "DATAIMP_NOT_APPROVED") ], EntityOperator.AND);
				 * conditionsList.add(statusCondition);
				 */
			}

			String fromDate = request.getParameter("fromDate");
			String thruDate = request.getParameter("thruDate");
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss.SSS");
			if (UtilValidate.isNotEmpty(fromDate)) {
				String fromdateVal = fromDate + " " + "00:00:00.0";
				Date parsedDateFrom = dateFormat.parse(fromdateVal);
				Timestamp fromTimestamp = new java.sql.Timestamp(parsedDateFrom.getTime());
				conditions.addAll(UtilMisc.toList(new EntityExpr("createdStamp", EntityOperator.NOT_EQUAL, null),
						new EntityExpr("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO, fromTimestamp)));
			}
			if (UtilValidate.isNotEmpty(thruDate)) {
				String thruDateVal = thruDate + " " + "23:23:59.0";
				Date parsedDateThru = dateFormat.parse(thruDateVal);
				Timestamp thruDateTimestamp = new java.sql.Timestamp(parsedDateThru.getTime());
				conditions.addAll(UtilMisc.toList(new EntityExpr("createdStamp", EntityOperator.NOT_EQUAL, null),
						new EntityExpr("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO, thruDateTimestamp)));
			}

			if (UtilValidate.isNotEmpty(partyId)) {
				EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(
						EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("leadId")),
								EntityOperator.LIKE, "%" + partyId.toUpperCase() + "%"),
						EntityCondition.makeCondition(
								EntityFunction.UPPER(EntityFieldValue.makeFieldValue("primaryPartyId")),
								EntityOperator.LIKE, "%" + partyId.toUpperCase() + "%")),
						EntityOperator.OR);
				conditions.add(condition);
			}

			if (UtilValidate.isNotEmpty(batchId)) {
				EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(
						EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("batchId")),
								EntityOperator.LIKE, "%" + batchId.toUpperCase() + "%")),
						EntityOperator.OR);
				conditions.add(condition);
			}

			if (UtilValidate.isNotEmpty(firstName)) {
				EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
						EntityFunction.UPPER(EntityFieldValue.makeFieldValue("firstName")), EntityOperator.LIKE,
						"%" + firstName.toUpperCase() + "%")), EntityOperator.OR);
				conditions.add(condition);
			}
			if (UtilValidate.isNotEmpty(lastName)) {
				EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(
						EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("lastName")),
								EntityOperator.LIKE, "%" + lastName.toUpperCase() + "%")),
						EntityOperator.OR);
				conditions.add(condition);
			}

			if (UtilValidate.isNotEmpty(errorCodeId)) {
				EntityCondition condition = EntityCondition
						.makeCondition(
								UtilMisc.toList(EntityCondition.makeCondition(
										EntityFunction.UPPER(EntityFieldValue.makeFieldValue("errorCodes")),
										EntityOperator.LIKE, "%" + errorCodeId.toUpperCase() + "%")),
								EntityOperator.OR);
				conditions.add(condition);
			}

			if (UtilValidate.isNotEmpty(uploadedByUserLoginId)) {
				conditions.add(EntityCondition.makeCondition("uploadedByUserLoginId", EntityOperator.EQUALS,
						uploadedByUserLoginId));
			}

			if (!isApprover) {
				conditions.add(EntityCondition.makeCondition("uploadedByUserLoginId", EntityOperator.EQUALS,
						userLogin.getString("userLoginId")));
			}

			/*
			 * EntityCondition orderFieldConditions = EntityCondition.makeCondition(
			 * EntityUtil.getFilterByDateExpr() ); conditions.add(orderFieldConditions);
			 */
			EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			EntityFindOptions efo = new EntityFindOptions();
			efo.setDistinct(true);

			System.out.println(mainConditons + " mainConditonsmainConditons");
			List<GenericValue> parties = delegator.findList("DataImportLeadSummary", mainConditons,
					UtilMisc.toSet("leadId"), null, null, false);

			System.out.println(parties + " partiesparties");
			if (parties != null && parties.size() > 0) {
				List<String> partyIdList = EntityUtil.getFieldListFromEntityList(parties, "leadId", true);
				List<Map<String, Object>> extractList = FastList.newInstance();
				if (partyIdList.size() > 0) {
					for (String leadId : partyIdList) {
						Map<String, Object> exportRow = new LinkedHashMap<String, Object>();

						GenericValue dataImportLeadSummary = EntityUtil.getFirst(delegator
								.findByAnd("DataImportLeadSummary", UtilMisc.toMap("leadId", leadId), null, false));
						if (UtilValidate.isNotEmpty(dataImportLeadSummary)) {
							exportRow.put("lead_id", leadId);

							ExportWrapper.getData(selectedFields, "batch_id",
									dataImportLeadSummary.getString("batchId"), exportRow);
							ExportWrapper.getData(selectedFields, "lead_id", dataImportLeadSummary.getString("leadId"),
									exportRow);
							ExportWrapper.getData(selectedFields, "first_name",
									dataImportLeadSummary.getString("firstName"), exportRow);
							ExportWrapper.getData(selectedFields, "last_name",
									dataImportLeadSummary.getString("lastName"), exportRow);
							// ExportWrapper.getData(selectedFields, "attn_name",
							// dataImportLeadSummary.getString("attnName"), exportRow);
							ExportWrapper.getData(selectedFields, "address1",
									dataImportLeadSummary.getString("address1"), exportRow);
							ExportWrapper.getData(selectedFields, "address2",
									dataImportLeadSummary.getString("address2"), exportRow);
							ExportWrapper.getData(selectedFields, "city", dataImportLeadSummary.getString("city"),
									exportRow);
							ExportWrapper.getData(selectedFields, "state_province_geoid",
									dataImportLeadSummary.getString("stateProvinceGeoId"), exportRow);
							ExportWrapper.getData(selectedFields, "postal_code",
									dataImportLeadSummary.getString("postalCode"), exportRow);
							ExportWrapper.getData(selectedFields, "postal_code_ext",
									dataImportLeadSummary.getString("postalCodeExt"), exportRow);
							ExportWrapper.getData(selectedFields, "state_province_geo_name",
									dataImportLeadSummary.getString("stateProvinceGeoName"), exportRow);
							ExportWrapper.getData(selectedFields, "country_geo_id",
									dataImportLeadSummary.getString("countryGeoId"), exportRow);
							ExportWrapper.getData(selectedFields, "primary_phone_country_code",
									dataImportLeadSummary.getString("primaryPhoneCountryCode"), exportRow);
							ExportWrapper.getData(selectedFields, "primary_phone_area_code",
									dataImportLeadSummary.getString("primaryPhoneAreaCode"), exportRow);
							ExportWrapper.getData(selectedFields, "primary_phone_number",
									dataImportLeadSummary.getString("primaryPhoneNumber"), exportRow);
							ExportWrapper.getData(selectedFields, "primary_phone_extension",
									dataImportLeadSummary.getString("primaryPhoneExtension"), exportRow);
							ExportWrapper.getData(selectedFields, "secondary_phone_country_code",
									dataImportLeadSummary.getString("secondaryPhoneCountryCode"), exportRow);
							ExportWrapper.getData(selectedFields, "secondary_phone_area_code",
									dataImportLeadSummary.getString("secondaryPhoneAreaCode"), exportRow);
							ExportWrapper.getData(selectedFields, "secondary_phone_number",
									dataImportLeadSummary.getString("secondaryPhoneNumber"), exportRow);
							ExportWrapper.getData(selectedFields, "secondary_phone_extension",
									dataImportLeadSummary.getString("secondaryPhoneExtension"), exportRow);
							/*
							 * ExportWrapper.getData(selectedFields, "fax_country_code",
							 * dataImportLeadSummary.getString("faxCountryCode"), exportRow);
							 * ExportWrapper.getData(selectedFields, "fax_area_code",
							 * dataImportLeadSummary.getString("faxAreaCode"), exportRow);
							 * ExportWrapper.getData(selectedFields, "fax_number",
							 * dataImportLeadSummary.getString("faxNumber"), exportRow);
							 * ExportWrapper.getData(selectedFields, "did_country_code",
							 * dataImportLeadSummary.getString("didCountryCode"), exportRow);
							 * ExportWrapper.getData(selectedFields, "did_area_code",
							 * dataImportLeadSummary.getString("didAreaCode"), exportRow);
							 * ExportWrapper.getData(selectedFields, "did_number",
							 * dataImportLeadSummary.getString("didNumber"), exportRow);
							 * ExportWrapper.getData(selectedFields, "did_extension",
							 * dataImportLeadSummary.getString("didExtension"), exportRow);
							 */
							ExportWrapper.getData(selectedFields, "email_address",
									dataImportLeadSummary.getString("emailAddress"), exportRow);
							/*
							 * ExportWrapper.getData(selectedFields, "web_address",
							 * dataImportLeadSummary.getString("webAddress"), exportRow);
							 * ExportWrapper.getData(selectedFields, "note",
							 * dataImportLeadSummary.getString("note"), exportRow);
							 * ExportWrapper.getData(selectedFields, "source",
							 * dataImportLeadSummary.getString("source"), exportRow);
							 */
							ExportWrapper.getData(selectedFields, "import_status_id",
									dataImportLeadSummary.getString("importStatusId"), exportRow);
							ExportWrapper.getData(selectedFields, "import_error",
									dataImportLeadSummary.getString("importError"), exportRow);
							ExportWrapper.getData(selectedFields, "primary_party_id",
									dataImportLeadSummary.getString("primaryPartyId"), exportRow);
							ExportWrapper.getData(selectedFields, "company_name",
									dataImportLeadSummary.getString("companyName"), exportRow);
							ExportWrapper.getData(selectedFields, "parent_co_details",
									dataImportLeadSummary.getString("parentCoDetails"), exportRow);
							ExportWrapper.getData(selectedFields, "sales_turn_over",
									dataImportLeadSummary.getString("salesTurnover"), exportRow);
							ExportWrapper.getData(selectedFields, "date_of_incorporation",
									dataImportLeadSummary.getString("dateOfIncorporation"), exportRow);
							ExportWrapper.getData(selectedFields, "constitution",
									dataImportLeadSummary.getString("constitution"), exportRow);
							ExportWrapper.getData(selectedFields, "industry_cat",
									dataImportLeadSummary.getString("industryCat"), exportRow);
							ExportWrapper.getData(selectedFields, "industry",
									dataImportLeadSummary.getString("industry"), exportRow);
							ExportWrapper.getData(selectedFields, "customer_trading_type",
									dataImportLeadSummary.getString("customerTradingType"), exportRow);
							ExportWrapper.getData(selectedFields, "tally_user_type",
									dataImportLeadSummary.getString("tallyUserType"), exportRow);
							ExportWrapper.getData(selectedFields, "tcp_name",
									dataImportLeadSummary.getString("tcpName"), exportRow);
							ExportWrapper.getData(selectedFields, "key_contact_person1",
									dataImportLeadSummary.getString("keyContactPerson1"), exportRow);
							ExportWrapper.getData(selectedFields, "key_contact_person2",
									dataImportLeadSummary.getString("keyContactPerson2"), exportRow);
							ExportWrapper.getData(selectedFields, "permanent_acccount_number",
									dataImportLeadSummary.getString("permanentAcccountNumber"), exportRow);
							ExportWrapper.getData(selectedFields, "business_reg_no",
									dataImportLeadSummary.getString("businessRegNo"), exportRow);
							ExportWrapper.getData(selectedFields, "other_bank_name",
									dataImportLeadSummary.getString("otherBankName"), exportRow);
							ExportWrapper.getData(selectedFields, "other_bank_balance",
									dataImportLeadSummary.getString("otherBankBalance"), exportRow);

							ExportWrapper.getData(selectedFields, "products_held_in_oth_bank",
									dataImportLeadSummary.getString("productsHeldInOthBank"), exportRow);
							ExportWrapper.getData(selectedFields, "products_value_in_oth_bank",
									dataImportLeadSummary.getString("productsValueInOthBank"), exportRow);
							ExportWrapper.getData(selectedFields, "paidup_capital",
									dataImportLeadSummary.getString("paidupCapital"), exportRow);
							ExportWrapper.getData(selectedFields, "authorised_cap",
									dataImportLeadSummary.getString("authorisedCap"), exportRow);
							ExportWrapper.getData(selectedFields, "lead_assign_to",
									dataImportLeadSummary.getString("leadAssignTo"), exportRow);
							ExportWrapper.getData(selectedFields, "lead_assign_by",
									dataImportLeadSummary.getString("leadAssignBy"), exportRow);
							ExportWrapper.getData(selectedFields, "segment", dataImportLeadSummary.getString("segment"),
									exportRow);
							ExportWrapper.getData(selectedFields, "liab_or_asset",
									dataImportLeadSummary.getString("liabOrAsset"), exportRow);
							ExportWrapper.getData(selectedFields, "tele_calling_status",
									dataImportLeadSummary.getString("teleCallingStatus"), exportRow);
							ExportWrapper.getData(selectedFields, "tele_calling_sub_status",
									dataImportLeadSummary.getString("teleCallingSubStatus"), exportRow);
							ExportWrapper.getData(selectedFields, "tele_calling_remarks",
									dataImportLeadSummary.getString("teleCallingRemarks"), exportRow);
							ExportWrapper.getData(selectedFields, "rm_calling_status",
									dataImportLeadSummary.getString("rmCallingStatus"), exportRow);
							ExportWrapper.getData(selectedFields, "rm_calling_sub_status",
									dataImportLeadSummary.getString("rmCallingSubStatus"), exportRow);
							ExportWrapper.getData(selectedFields, "rm_calling_remarks",
									dataImportLeadSummary.getString("rmCallingRemarks"), exportRow);
							ExportWrapper.getData(selectedFields, "title", dataImportLeadSummary.getString("title"),
									exportRow);
							ExportWrapper.getData(selectedFields, "no_of_attempt",
									dataImportLeadSummary.getString("noOfAttempt"), exportRow);
							ExportWrapper.getData(selectedFields, "finacle_id",
									dataImportLeadSummary.getString("finacleId"), exportRow);
							ExportWrapper.getData(selectedFields, "place_of_incorporation",
									dataImportLeadSummary.getString("placeOfIncorporation"), exportRow);
							ExportWrapper.getData(selectedFields, "no_of_employees",
									dataImportLeadSummary.getString("noOfEmployees"), exportRow);

							ExportWrapper.getData(selectedFields, "job_family",
									dataImportLeadSummary.getString("jobFamily"), exportRow);
							ExportWrapper.getData(selectedFields, "designation",
									dataImportLeadSummary.getString("designation"), exportRow);
							ExportWrapper.getData(selectedFields, "lead_status",
									dataImportLeadSummary.getString("leadStatus"), exportRow);
							ExportWrapper.getData(selectedFields, "error_codes",
									dataImportLeadSummary.getString("errorCodes"), exportRow);
							/*
							 * ExportWrapper.getData(selectedFields, "approved_by_user_login_id",
							 * dataImportLeadSummary.getString("approvedByUserLoginId"), exportRow);
							 * ExportWrapper.getData(selectedFields, "rejected_by_user_login_id",
							 * dataImportLeadSummary.getString("rejectedByUserLoginId"), exportRow);
							 * ExportWrapper.getData(selectedFields, "uploaded_by_user_login_id",
							 * dataImportLeadSummary.getString("uploadedByUserLoginId"), exportRow);
							 */
							ExportWrapper.getData(selectedFields, "is_dedup_auto_segmentation",
									dataImportLeadSummary.getString("isDedupAutoSegmentation"), exportRow);

							extractList.add(exportRow);
							System.out.println(exportRow + " exportRowexportRowexportRow");
							System.out.println(extractList + " extractListextractList");
						}
					}
				}

				if (extractList.size() > 0) {

					String fileName = "LeadErrorExport_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
							+ ExportUtil.getFileExtension(exportType);
					String location = ComponentConfig.getRootLocation("crm") + "/webapp/crm-resource/template";
					String delimiter = ",";

					Map<String, Object> exportContext = new HashMap<String, Object>();

					exportContext.put("delegator", delegator);
					exportContext.put("rows", extractList);
					exportContext.put("headers", null);
					exportContext.put("fileName", fileName);
					exportContext.put("location", location);
					exportContext.put("delimiter", delimiter);
					exportContext.put("isHeaderRequird", true);

					exportContext.put("exportType", exportType);

					ExporterFacade.exportReport(exportContext);

					Thread.sleep(1000);

					String filePath = location + File.separatorChar + fileName;

					ExporterFacade.downloadReport(request, response, filePath, exportType);

					boolean isdelete = true;
					if (isdelete) {
						File file = new File(filePath);
						file.delete();
					}
				}
			}

		} catch (Exception e) {
			Debug.logError("Error : " + e.getMessage(), module);
			return "error";
		}
		return "success";
	}

	/** Method to get Contact Email for Contact Person */
	private static String getEmail(Delegator delegator, String partyId) throws GenericEntityException {
		String email = "";
		GenericValue personEmailList = EntityUtil.getFirst(delegator.findByAnd("PartyContactMechPurpose",
				UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_EMAIL"), null, false));
		if (UtilValidate.isNotEmpty(personEmailList)) {
			GenericValue personEmail = EntityUtil.getFirst(delegator.findByAnd("ContactMech",
					UtilMisc.toMap("contactMechId", personEmailList.getString("contactMechId")), null, false));
			if (UtilValidate.isNotEmpty(personEmail))
				email = personEmail.getString("infoString");
		}
		return email;
	}

	/** Method to get Contact Mobile for Contact Person */
	private static String getPhone(Delegator delegator, String partyId) throws GenericEntityException {
		String phone = "";
		GenericValue personPhoneList = EntityUtil.getFirst(delegator.findByAnd("PartyContactMechPurpose",
				UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_PHONE"), null, false));
		if (UtilValidate.isNotEmpty(personPhoneList)) {
			GenericValue personPhone = EntityUtil.getFirst(delegator.findByAnd("TelecomNumber",
					UtilMisc.toMap("contactMechId", personPhoneList.getString("contactMechId")), null, false));
			if (UtilValidate.isNotEmpty(personPhone))
				phone = personPhone.getString("contactNumber");
		}
		return phone;
	}

	/** Method to get CompanyCIN, GSTN and IECCode for Specific Lead */
	public static Map<String, String> getPartyAttrs(Delegator delegator, String leadId) {

		Map<String, String> moAttrs = new HashMap<String, String>();
		moAttrs.put("gstn", "");
		moAttrs.put("iecCode", "");
		moAttrs.put("cin", "");

		try {
			List<GenericValue> partyAttrs = delegator.findByAnd("PartyIdentification", UtilMisc.toMap("partyId", leadId),
					null, false);
			Debug.log("party attrs == > " + moAttrs);

			if (UtilValidate.isNotEmpty(partyAttrs)) {
				//moAttrs = partyAttrs.stream().collect(Collectors.toMap(x -> (String) x.get("attrName"),
						moAttrs = partyAttrs.stream().collect(Collectors.toMap(x -> (String) x.get("partyIdentificationTypeId"),
								//x -> UtilValidate.isNotEmpty((String) x.get("attrValue")) ? (String) x.get("attrValue") : "",
										x -> UtilValidate.isNotEmpty((String) x.get("idValue")) ? (String) x.get("idValue") : "",
						(attr1, attr2) -> {
							return attr2;
						}));
			}

		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return moAttrs;
	}
}
