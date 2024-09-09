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

package org.fio.dataimport;

import org.apache.commons.lang.StringUtils;
import org.fio.dataimport.util.DataUtil;
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.UtilParty;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.datasource.GenericHelperInfo;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class LeadImportServices {

	public static String module = LeadImportServices.class.getName();

	public static Map<String, Object> importLeads(DispatchContext dctx, Map<String, ?> context) {

		int imported = 0;
		OpentapsImporter leadImporter = null;
		try {
			// then import the tax rates for the counties
			leadImporter = new OpentapsImporter("DataImportLead", dctx, new LeadDecoder(context));
			imported += leadImporter.runImport(context);
		} catch (GenericEntityException e) {
			return UtilMessage.createAndLogServiceError(e, module);
		}

		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("importedRecords", imported);
		result.put("importedDataList", leadImporter.importedDataList);

		return result;
	}
}

// maps DataImportLead into a set of opentaps entities that describes the Lead
class LeadDecoder implements ImportDecoder {
	
	public static final String module = LeadDecoder.class.getName();
	
	protected GenericValue userLogin;
	
	public LeadDecoder(Map<String, ?> context) {
    	this.userLogin = (GenericValue) context.get("userLogin");
    }

	public List<GenericValue> decode(GenericValue entry, Timestamp importTimestamp, Delegator delegator,
			LocalDispatcher dispatcher, Object... args) throws Exception {
		
		//String fullName = UtilImport.getFullName(entry.getString("firstName"), entry.getString("lastName"));
		String fullName = entry.getString("companyName");
		
		List<GenericValue> toBeStored = FastList.newInstance();

		Debug.logInfo("Now processing  Lead name [" + entry.getString("companyName") + "]", module);
		
		List<EntityCondition> conditions = new ArrayList<EntityCondition>();
		EntityCondition mainConditon = null;

		String partyId = UtilParty.getPartyIdByDedupLogic(delegator, entry.getString("leadId"), "EXT_PARTY_ID", entry.getString("emailAddress"), entry.getString("primaryPhoneNumber"), fullName, true);
		
		/*
		conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
				EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, entry.getString("leadId")),
				EntityCondition.makeCondition("externalId", EntityOperator.EQUALS, entry.getString("leadId"))
				));
    	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);*/
		
    	GenericValue party = EntityQuery.use(delegator).from("Party").where(mainConditon).queryFirst();
		if (UtilValidate.isNotEmpty(party)) {
			partyId = party.getString("partyId");
		}
		
		if (UtilValidate.isNotEmpty(partyId)) {
			GenericValue partySupplementalData = EntityUtil.getFirst(
					delegator.findByAnd("PartySupplementalData", UtilMisc.toMap("partyId", partyId), null, false));
			if (UtilValidate.isEmpty(partySupplementalData)) {
				partySupplementalData = delegator.makeValue("PartySupplementalData",
						UtilMisc.toMap("partyId", partyId));
			}

			partySupplementalData.put("companyName", entry.getString("companyName"));
			partySupplementalData.put("numberEmployees", entry.getLong("noOfEmployees"));
			partySupplementalData.put("annualRevenue", entry.get("annualRevenue"));
			
			party.put("description", entry.getString("description"));
			party.put("preferredCurrencyUomId", entry.getString("currencyUomId"));
			party.put("timeZoneDesc", entry.getString("timeZoneId"));
			
			toBeStored.add(party);
			
			String personResponsible = entry.getString("personResponsible");
			if (UtilValidate.isNotEmpty(personResponsible)) {
				conditions = new ArrayList<EntityCondition>();
				conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId));
				conditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "LEAD"));
				conditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"));
				conditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.NOT_EQUAL, personResponsible));
				//conditions.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "EMPLOYEE");
				conditions.add(EntityUtil.getFilterByDateExpr());
		    	mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
		    	List<GenericValue> partyRelList = EntityQuery.use(delegator).from("PartyRelationship").where(mainConditon).queryList();
				if (UtilValidate.isNotEmpty(partyRelList)) {
					for (GenericValue partyRel : partyRelList) {
						partyRel.put("thruDate", UtilDateTime.nowTimestamp());
						toBeStored.add(partyRel);
					}
				}
				
				conditions = new ArrayList<EntityCondition>();
				conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId));
				conditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "LEAD"));
				conditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"));
				conditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, personResponsible));
				//conditions.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "EMPLOYEE");
				conditions.add(EntityUtil.getFilterByDateExpr());
		    	mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
		    	GenericValue partyRel = EntityQuery.use(delegator).from("PartyRelationship").where(mainConditon).queryFirst();
				if (UtilValidate.isEmpty(partyRel)) {
					String responsibleRoleType = PartyHelper.getPartyRoleTypeId(personResponsible, delegator);
					if (UtilValidate.isEmpty(responsibleRoleType)) {
						responsibleRoleType = "EMPLOYEE";
					}
					Map<String, Object> partyRelationship = UtilMisc.toMap("partyIdTo", personResponsible, "roleTypeIdTo", responsibleRoleType,
							"partyIdFrom", partyId, "roleTypeIdFrom", "LEAD", "partyRelationshipTypeId", "RESPONSIBLE_FOR",
							"fromDate", importTimestamp);
					partyRelationship.put("securityGroupId", "ACCOUNT_OWNER");
					toBeStored.add(delegator.makeValue("PartyRelationship", partyRelationship));
				}
			}

			//GenericValue userLogin = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", "admin"));
			// update basic details
			
			GenericValue partyGroup = EntityUtil.getFirst(
					delegator.findByAnd("PartyGroup", UtilMisc.toMap("partyId", partyId), null, false));
			if (UtilValidate.isNotEmpty(partyGroup)) {
				//input.put("groupName", UtilImport.getFullName(entry.getString("firstName"), entry.getString("lastName")));
				partyGroup.put("groupName", fullName);
				partyGroup.put("groupNameLocal", entry.getString("groupNameLocal"));
				
				toBeStored.add(partyGroup);
			}
			
			// update phone contact
			GenericValue findShippingContactMech = EntityUtil.getFirst(delegator.findByAnd("PartyContactMechPurpose",
					UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_PHONE"), null, false));
			if (UtilValidate.isNotEmpty(findShippingContactMech)) {
				String contactMechId = findShippingContactMech.getString("contactMechId");

				// check and store dnd status in primay phone number
				Map<String, Object> dndStatusMp = DataUtil.getDndStatus(delegator,
						entry.getString("primaryPhoneNumber"));

				GenericValue phoneContactMech = delegator.findOne("TelecomNumber", false,
						UtilMisc.toMap("contactMechId", contactMechId));
				// phoneContactMech.put("contactMechId", contactMechId);
				phoneContactMech.put("countryCode", entry.getString("primaryPhoneCountryCode"));
				phoneContactMech.put("areaCode", entry.getString("primaryPhoneAreaCode"));
				phoneContactMech.put("contactNumber", entry.getString("primaryPhoneNumber"));
				phoneContactMech.put("askForName", entry.getString("keyContactPerson1"));
				phoneContactMech.put("dndStatus", dndStatusMp.get("dndStatus"));
				phoneContactMech.store();

				String dndIndicator = (String) dndStatusMp.get("dndIndicator");
				String dndSeqId = (String) dndStatusMp.get("dndSeqId");
				Boolean validateDndAuditLogDetails = DataUtil.validateDndAuditLogDetails(delegator,
						entry.getString("primaryPhoneNumber"), partyId, dndIndicator);
				if (UtilValidate.isNotEmpty(dndSeqId) && UtilValidate.isNotEmpty(dndIndicator)
						&& validateDndAuditLogDetails) {
					toBeStored.add(DataUtil.makeDndAuditLogDetails(dndSeqId, partyId, "UPDATE",
							entry.getString("primaryPhoneNumber"), dndIndicator, importTimestamp, delegator));
				}

				GenericValue partyContactMech = EntityUtil.getFirst(delegator.findByAnd("PartyContactMech",
						UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId), null, false));
				if (UtilValidate.isNotEmpty(partyContactMech)) {
					partyContactMech.put("extension", entry.getString("primaryPhoneExtension"));
					partyContactMech.put("allowSolicitation", dndStatusMp.get("solicitationStatus"));
					partyContactMech.store();
				}

			} else if (!UtilValidate.isEmpty(entry.getString("primaryPhoneNumber"))) {
				// associate this as PRIMARY_PHONE
				GenericValue contactMech = delegator.makeValue("ContactMech", UtilMisc.toMap("contactMechId",
						delegator.getNextSeqId("ContactMech"), "contactMechTypeId", "TELECOM_NUMBER"));
				String telecomContactMechId = contactMech.getString("contactMechId");
				GenericValue primaryNumber = UtilImport.makeTelecomNumber(contactMech,
						entry.getString("primaryPhoneCountryCode"), entry.getString("primaryPhoneAreaCode"),
						entry.getString("primaryPhoneNumber"), delegator);
				primaryNumber.put("askForName", entry.getString("keyContactPerson1"));

				// check and store dnd status in primay phone number
				Map<String, Object> dndStatusMp = DataUtil.getDndStatus(delegator,
						entry.getString("primaryPhoneNumber"));
				primaryNumber.put("dndStatus", dndStatusMp.get("dndStatus"));
				String dndIndicator = (String) dndStatusMp.get("dndIndicator");
				String dndSeqId = (String) dndStatusMp.get("dndSeqId");
				if (UtilValidate.isNotEmpty(dndSeqId) && UtilValidate.isNotEmpty(dndIndicator)) {
					toBeStored.add(DataUtil.makeDndAuditLogDetails(dndSeqId, partyId, "CREATE",
							entry.getString("primaryPhoneNumber"), dndIndicator, importTimestamp, delegator));
				}

				toBeStored.add(contactMech);
				toBeStored.add(primaryNumber);

				toBeStored.add(UtilImport.makeContactMechPurpose("PRIMARY_PHONE", primaryNumber, partyId,
						importTimestamp, delegator));
				toBeStored.add(delegator.makeValue("PartyContactMech",
						UtilMisc.toMap("contactMechId", telecomContactMechId, "partyId", partyId, "fromDate",
								importTimestamp, "extension", entry.getString("primaryPhoneExtension"),
								"allowSolicitation", dndStatusMp.get("solicitationStatus"))));
				partySupplementalData.set("primaryTelecomNumberId", telecomContactMechId);
			}

			// update secondary number

			GenericValue findSecondaryContactMech = EntityUtil.getFirst(delegator.findByAnd("PartyContactMechPurpose",
					UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PHONE_WORK_SEC"), null, false));
			if (UtilValidate.isNotEmpty(findSecondaryContactMech)) {
				String contactMechId = findSecondaryContactMech.getString("contactMechId");

				// check and store dnd status in primay phone number
				Map<String, Object> dndStatusMp = DataUtil.getDndStatus(delegator,
						entry.getString("secondaryPhoneNumber"));

				GenericValue phoneContactMech = delegator.findOne("TelecomNumber", false,
						UtilMisc.toMap("contactMechId", contactMechId));
				phoneContactMech.put("countryCode", entry.getString("secondaryPhoneCountryCode"));
				phoneContactMech.put("areaCode", entry.getString("secondaryPhoneAreaCode"));
				phoneContactMech.put("contactNumber", entry.getString("secondaryPhoneNumber"));
				phoneContactMech.put("askForName", entry.getString("keyContactPerson2"));
				phoneContactMech.put("dndStatus", dndStatusMp.get("dndStatus"));
				phoneContactMech.store();

				String dndIndicator = (String) dndStatusMp.get("dndIndicator");
				String dndSeqId = (String) dndStatusMp.get("dndSeqId");
				Boolean validateDndAuditLogDetails = DataUtil.validateDndAuditLogDetails(delegator,
						entry.getString("secondaryPhoneNumber"), partyId, dndIndicator);
				if (UtilValidate.isNotEmpty(dndSeqId) && UtilValidate.isNotEmpty(dndIndicator)
						&& validateDndAuditLogDetails) {
					toBeStored.add(DataUtil.makeDndAuditLogDetails(dndSeqId, partyId, "UPDATE",
							entry.getString("secondaryPhoneNumber"), dndIndicator, importTimestamp, delegator));
				}

				GenericValue partyContactMech = EntityUtil.getFirst(delegator.findByAnd("PartyContactMech",
						UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId), null, false));
				if (UtilValidate.isNotEmpty(partyContactMech)) {
					partyContactMech.put("extension", entry.getString("secondaryPhoneExtension"));
					partyContactMech.put("allowSolicitation", dndStatusMp.get("solicitationStatus"));
					partyContactMech.store();
				}
			} else if (!UtilValidate.isEmpty(entry.getString("secondaryPhoneNumber"))) {
				// this one has no contactmech purpose type
				GenericValue contactMech = delegator.makeValue("ContactMech", UtilMisc.toMap("contactMechId",
						delegator.getNextSeqId("ContactMech"), "contactMechTypeId", "TELECOM_NUMBER"));

				// check and store dnd status in secondary phone number
				Map<String, Object> dndStatusMp = DataUtil.getDndStatus(delegator,
						entry.getString("secondaryPhoneNumber"));

				GenericValue secondaryNumber = UtilImport.makeTelecomNumber(contactMech,
						entry.getString("secondaryPhoneCountryCode"), entry.getString("secondaryPhoneAreaCode"),
						entry.getString("secondaryPhoneNumber"), delegator);
				secondaryNumber.put("askForName", entry.getString("keyContactPerson2"));
				secondaryNumber.put("dndStatus", dndStatusMp.get("dndStatus"));

				toBeStored.add(contactMech);
				toBeStored.add(secondaryNumber);

				String dndIndicator = (String) dndStatusMp.get("dndIndicator");
				String dndSeqId = (String) dndStatusMp.get("dndSeqId");
				if (UtilValidate.isNotEmpty(dndSeqId) && UtilValidate.isNotEmpty(dndIndicator)) {
					toBeStored.add(DataUtil.makeDndAuditLogDetails(dndSeqId, partyId, "CREATE",
							entry.getString("secondaryPhoneNumber"), dndIndicator, importTimestamp, delegator));
				}

				toBeStored.add(UtilImport.makeContactMechPurpose("PHONE_WORK_SEC", secondaryNumber, partyId,
						importTimestamp, delegator));
				toBeStored.add(delegator.makeValue("PartyContactMech",
						UtilMisc.toMap("contactMechId", contactMech.get("contactMechId"), "partyId", partyId,
								"fromDate", importTimestamp, "extension", entry.getString("secondaryPhoneExtension"),
								"allowSolicitation", dndStatusMp.get("solicitationStatus"))));
			}

			// update email
			GenericValue emailPurpose = EntityUtil.getFirst(delegator.findByAnd("PartyContactMechPurpose",
					UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_EMAIL"), null, false));
			if (UtilValidate.isNotEmpty(emailPurpose)) {
				GenericValue emailContactMech = delegator.findOne("ContactMech", false,
						UtilMisc.toMap("contactMechId", emailPurpose.getString("contactMechId")));
				emailContactMech.put("infoString", entry.getString("emailAddress"));
				emailContactMech.store();
			} else if (!UtilValidate.isEmpty(entry.getString("emailAddress"))) {
				GenericValue emailContactMech = delegator.makeValue("ContactMech",
						UtilMisc.toMap("contactMechId", delegator.getNextSeqId("ContactMech"), "contactMechTypeId",
								"EMAIL_ADDRESS", "infoString", entry.getString("emailAddress")));
				String emailContactMechId = emailContactMech.getString("contactMechId");
				toBeStored.add(emailContactMech);
				toBeStored
						.add(delegator.makeValue("PartyContactMech", UtilMisc.toMap("contactMechId", emailContactMechId,
								"partyId", partyId, "fromDate", importTimestamp, "allowSolicitation", "Y")));
				toBeStored.add(UtilImport.makeContactMechPurpose("PRIMARY_EMAIL", emailContactMech, partyId,
						importTimestamp, delegator));
			}

			// update webAddress
			GenericValue webPurpose = EntityUtil.getFirst(delegator.findByAnd("PartyContactMechPurpose",
					UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_WEB_URL"), null, false));
			if (UtilValidate.isNotEmpty(webPurpose)) {
				// webContactMech.put("contactMechId",
				// webPurpose.getString("contactMechId"));
				GenericValue webContactMech = delegator.findOne("ContactMech", false,
						UtilMisc.toMap("contactMechId", webPurpose.getString("contactMechId")));
				webContactMech.put("infoString", entry.getString("webAddress"));
				webContactMech.store();
			} else if (!UtilValidate.isEmpty(entry.getString("webAddress"))) {
				GenericValue webContactMech = delegator.makeValue("ContactMech",
						UtilMisc.toMap("contactMechId", delegator.getNextSeqId("ContactMech"), "contactMechTypeId",
								"WEB_ADDRESS", "infoString", entry.getString("webAddress")));
				toBeStored.add(webContactMech);
				toBeStored.add(delegator.makeValue("PartyContactMech",
						UtilMisc.toMap("contactMechId", webContactMech.get("contactMechId"), "partyId", partyId,
								"fromDate", importTimestamp, "allowSolicitation", "Y")));
				toBeStored.add(UtilImport.makeContactMechPurpose("PRIMARY_WEB_URL", webContactMech, partyId,
						importTimestamp, delegator));
			}
			
			// Social media [start]
			// facebook contact
			GenericValue socialMediaPurpose = EntityUtil.getFirst(delegator.findByAnd("PartyContactMechPurpose",
					UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "SOCIAL_FACEBOOK"), null, false));
			if (UtilValidate.isNotEmpty(socialMediaPurpose)) {
				GenericValue socialMediaContactMech = delegator.findOne("ContactMech", false,
						UtilMisc.toMap("contactMechId", socialMediaPurpose.getString("contactMechId")));
				socialMediaContactMech.put("infoString", entry.getString("facebookContact"));
				socialMediaContactMech.store();
			} else if (!UtilValidate.isEmpty(entry.getString("facebookContact"))) {
				GenericValue socialMediaContactMech = delegator.makeValue("ContactMech",
						UtilMisc.toMap("contactMechId", delegator.getNextSeqId("ContactMech"), "contactMechTypeId",
								"SOCIAL_MEDIA_TYPE", "infoString", entry.getString("facebookContact")));
				String socialMediaContactMechId = socialMediaContactMech.getString("contactMechId");
				toBeStored.add(socialMediaContactMech);
				toBeStored.add(delegator.makeValue("PartyContactMech", UtilMisc.toMap("contactMechId", socialMediaContactMechId,
								"partyId", partyId, "fromDate", importTimestamp, "allowSolicitation", "Y")));
				toBeStored.add(UtilImport.makeContactMechPurpose("SOCIAL_FACEBOOK", socialMediaContactMech, partyId,
						importTimestamp, delegator));
			}
			
			// google contact
			socialMediaPurpose = EntityUtil.getFirst(delegator.findByAnd("PartyContactMechPurpose",
					UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "SOCIAL_GOOGLE"), null, false));
			if (UtilValidate.isNotEmpty(socialMediaPurpose)) {
				GenericValue socialMediaContactMech = delegator.findOne("ContactMech", false,
						UtilMisc.toMap("contactMechId", socialMediaPurpose.getString("contactMechId")));
				socialMediaContactMech.put("infoString", entry.getString("googleContact"));
				socialMediaContactMech.store();
			} else if (!UtilValidate.isEmpty(entry.getString("googleContact"))) {
				GenericValue socialMediaContactMech = delegator.makeValue("ContactMech",
						UtilMisc.toMap("contactMechId", delegator.getNextSeqId("ContactMech"), "contactMechTypeId",
								"SOCIAL_MEDIA_TYPE", "infoString", entry.getString("googleContact")));
				String socialMediaContactMechId = socialMediaContactMech.getString("contactMechId");
				toBeStored.add(socialMediaContactMech);
				toBeStored.add(delegator.makeValue("PartyContactMech", UtilMisc.toMap("contactMechId", socialMediaContactMechId,
								"partyId", partyId, "fromDate", importTimestamp, "allowSolicitation", "Y")));
				toBeStored.add(UtilImport.makeContactMechPurpose("SOCIAL_GOOGLE", socialMediaContactMech, partyId,
						importTimestamp, delegator));
			}
			
			// instagram contact
			socialMediaPurpose = EntityUtil.getFirst(delegator.findByAnd("PartyContactMechPurpose",
					UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "SOCIAL_INSTAGRAM"), null, false));
			if (UtilValidate.isNotEmpty(socialMediaPurpose)) {
				GenericValue socialMediaContactMech = delegator.findOne("ContactMech", false,
						UtilMisc.toMap("contactMechId", socialMediaPurpose.getString("contactMechId")));
				socialMediaContactMech.put("infoString", entry.getString("instagramContact"));
				socialMediaContactMech.store();
			} else if (!UtilValidate.isEmpty(entry.getString("instagramContact"))) {
				GenericValue socialMediaContactMech = delegator.makeValue("ContactMech",
						UtilMisc.toMap("contactMechId", delegator.getNextSeqId("ContactMech"), "contactMechTypeId",
								"SOCIAL_MEDIA_TYPE", "infoString", entry.getString("instagramContact")));
				String socialMediaContactMechId = socialMediaContactMech.getString("contactMechId");
				toBeStored.add(socialMediaContactMech);
				toBeStored.add(delegator.makeValue("PartyContactMech", UtilMisc.toMap("contactMechId", socialMediaContactMechId,
								"partyId", partyId, "fromDate", importTimestamp, "allowSolicitation", "Y")));
				toBeStored.add(UtilImport.makeContactMechPurpose("SOCIAL_INSTAGRAM", socialMediaContactMech, partyId,
						importTimestamp, delegator));
			}
			
			// linkedIn contact
			socialMediaPurpose = EntityUtil.getFirst(delegator.findByAnd("PartyContactMechPurpose",
					UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "SOCIAL_LINKEDIN"), null, false));
			if (UtilValidate.isNotEmpty(socialMediaPurpose)) {
				GenericValue socialMediaContactMech = delegator.findOne("ContactMech", false,
						UtilMisc.toMap("contactMechId", socialMediaPurpose.getString("contactMechId")));
				socialMediaContactMech.put("infoString", entry.getString("linkedInContact"));
				socialMediaContactMech.store();
			} else if (!UtilValidate.isEmpty(entry.getString("linkedInContact"))) {
				GenericValue socialMediaContactMech = delegator.makeValue("ContactMech",
						UtilMisc.toMap("contactMechId", delegator.getNextSeqId("ContactMech"), "contactMechTypeId",
								"SOCIAL_MEDIA_TYPE", "infoString", entry.getString("linkedInContact")));
				String socialMediaContactMechId = socialMediaContactMech.getString("contactMechId");
				toBeStored.add(socialMediaContactMech);
				toBeStored.add(delegator.makeValue("PartyContactMech", UtilMisc.toMap("contactMechId", socialMediaContactMechId,
								"partyId", partyId, "fromDate", importTimestamp, "allowSolicitation", "Y")));
				toBeStored.add(UtilImport.makeContactMechPurpose("SOCIAL_LINKEDIN", socialMediaContactMech, partyId,
						importTimestamp, delegator));
			}
			
			// twitter contact
			socialMediaPurpose = EntityUtil.getFirst(delegator.findByAnd("PartyContactMechPurpose",
					UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "SOCIAL_TWITTER"), null, false));
			if (UtilValidate.isNotEmpty(socialMediaPurpose)) {
				GenericValue socialMediaContactMech = delegator.findOne("ContactMech", false,
						UtilMisc.toMap("contactMechId", socialMediaPurpose.getString("contactMechId")));
				socialMediaContactMech.put("infoString", entry.getString("twitterContact"));
				socialMediaContactMech.store();
			} else if (!UtilValidate.isEmpty(entry.getString("twitterContact"))) {
				GenericValue socialMediaContactMech = delegator.makeValue("ContactMech",
						UtilMisc.toMap("contactMechId", delegator.getNextSeqId("ContactMech"), "contactMechTypeId",
								"SOCIAL_MEDIA_TYPE", "infoString", entry.getString("twitterContact")));
				String socialMediaContactMechId = socialMediaContactMech.getString("contactMechId");
				toBeStored.add(socialMediaContactMech);
				toBeStored.add(delegator.makeValue("PartyContactMech", UtilMisc.toMap("contactMechId", socialMediaContactMechId,
								"partyId", partyId, "fromDate", importTimestamp, "allowSolicitation", "Y")));
				toBeStored.add(UtilImport.makeContactMechPurpose("SOCIAL_TWITTER", socialMediaContactMech, partyId,
						importTimestamp, delegator));
			}
			
			// youtube contact
			socialMediaPurpose = EntityUtil.getFirst(delegator.findByAnd("PartyContactMechPurpose",
					UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "SOCIAL_YOUTUBE"), null, false));
			if (UtilValidate.isNotEmpty(socialMediaPurpose)) {
				GenericValue socialMediaContactMech = delegator.findOne("ContactMech", false,
						UtilMisc.toMap("contactMechId", socialMediaPurpose.getString("contactMechId")));
				socialMediaContactMech.put("infoString", entry.getString("youtubeContact"));
				socialMediaContactMech.store();
			} else if (!UtilValidate.isEmpty(entry.getString("youtubeContact"))) {
				GenericValue socialMediaContactMech = delegator.makeValue("ContactMech",
						UtilMisc.toMap("contactMechId", delegator.getNextSeqId("ContactMech"), "contactMechTypeId",
								"SOCIAL_MEDIA_TYPE", "infoString", entry.getString("youtubeContact")));
				String socialMediaContactMechId = socialMediaContactMech.getString("contactMechId");
				toBeStored.add(socialMediaContactMech);
				toBeStored.add(delegator.makeValue("PartyContactMech", UtilMisc.toMap("contactMechId", socialMediaContactMechId,
								"partyId", partyId, "fromDate", importTimestamp, "allowSolicitation", "Y")));
				toBeStored.add(UtilImport.makeContactMechPurpose("SOCIAL_YOUTUBE", socialMediaContactMech, partyId,
						importTimestamp, delegator));
			}
			
			// Social media [end]

			// update fax contact
			GenericValue findFaxContactMech = EntityUtil.getFirst(delegator.findByAnd("PartyContactMechPurpose",
					UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "FAX_NUMBER"), null, false));
			if (UtilValidate.isNotEmpty(findFaxContactMech)) {
				String contactMechId = findFaxContactMech.getString("contactMechId");
				GenericValue faxContactMech = delegator.findOne("TelecomNumber", false,
						UtilMisc.toMap("contactMechId", contactMechId));
				faxContactMech.put("countryCode", entry.getString("faxCountryCode"));
				faxContactMech.put("areaCode", entry.getString("faxAreaCode"));
				faxContactMech.put("contactNumber", entry.getString("faxNumber"));
				faxContactMech.store();
			} else if (!UtilValidate.isEmpty(entry.getString("faxNumber"))) {
				// associate this as FAX_NUMBER
				GenericValue contactMech = delegator.makeValue("ContactMech", UtilMisc.toMap("contactMechId",
						delegator.getNextSeqId("ContactMech"), "contactMechTypeId", "TELECOM_NUMBER"));
				GenericValue faxNumber = UtilImport.makeTelecomNumber(contactMech, entry.getString("faxCountryCode"),
						entry.getString("faxAreaCode"), entry.getString("faxNumber"), delegator);
				toBeStored.add(contactMech);
				toBeStored.add(faxNumber);
				toBeStored.add(UtilImport.makeContactMechPurpose("FAX_NUMBER", faxNumber, partyId, importTimestamp,
						delegator));
				toBeStored.add(delegator.makeValue("PartyContactMech",
						UtilMisc.toMap("contactMechId", contactMech.get("contactMechId"), "partyId", partyId,
								"fromDate", importTimestamp, "allowSolicitation", "Y")));
			}

			// update did contact
			GenericValue findDidContactMech = EntityUtil.getFirst(delegator.findByAnd("PartyContactMechPurpose",
					UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PHONE_DID"), null, false));
			if (UtilValidate.isNotEmpty(findDidContactMech)) {
				String contactMechId = findDidContactMech.getString("contactMechId");
				GenericValue didContactMech = delegator.findOne("TelecomNumber", false,
						UtilMisc.toMap("contactMechId", contactMechId));
				didContactMech.put("countryCode", entry.getString("didCountryCode"));
				didContactMech.put("areaCode", entry.getString("didAreaCode"));
				didContactMech.put("contactNumber", entry.getString("didNumber"));
				didContactMech.store();

				GenericValue partyContactMech = EntityUtil.getFirst(delegator.findByAnd("PartyContactMech",
						UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId), null, false));
				if (UtilValidate.isNotEmpty(partyContactMech)) {
					partyContactMech.put("extension", entry.getString("didExtension"));
					partyContactMech.store();
				}

			} else if (!UtilValidate.isEmpty(entry.getString("didNumber"))) {
				// associate this as PHONE_DID
				GenericValue contactMech = delegator.makeValue("ContactMech", UtilMisc.toMap("contactMechId",
						delegator.getNextSeqId("ContactMech"), "contactMechTypeId", "TELECOM_NUMBER"));
				GenericValue didNumber = UtilImport.makeTelecomNumber(contactMech, entry.getString("didCountryCode"),
						entry.getString("didAreaCode"), entry.getString("didNumber"), delegator);
				toBeStored.add(contactMech);
				toBeStored.add(didNumber);

				toBeStored.add(
						UtilImport.makeContactMechPurpose("PHONE_DID", didNumber, partyId, importTimestamp, delegator));
				toBeStored.add(delegator.makeValue("PartyContactMech",
						UtilMisc.toMap("contactMechId", contactMech.get("contactMechId"), "partyId", partyId,
								"fromDate", importTimestamp, "extension", entry.getString("didExtension"),
								"allowSolicitation", "Y")));
			}

			// update Postal Address
			GenericValue findPostalContactMech = EntityUtil.getFirst(delegator.findByAnd("PartyContactMechPurpose",
					UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "GENERAL_LOCATION"), null, false));
			if (UtilValidate.isNotEmpty(findPostalContactMech)) {
				String contactMechId = findPostalContactMech.getString("contactMechId");
				GenericValue postalContactMech = delegator.findOne("PostalAddress", false,
						UtilMisc.toMap("contactMechId", contactMechId));
				postalContactMech.put("toName", fullName);
				postalContactMech.put("attnName", entry.getString("attnName"));
				postalContactMech.put("address1", entry.getString("address1"));
				postalContactMech.put("address2", entry.getString("address2"));
				postalContactMech.put("city", entry.getString("city"));
				postalContactMech.put("postalCode", entry.getString("postalCode"));
				postalContactMech.put("postalCodeExt", entry.getString("postalCodeExt"));
				postalContactMech.put("countryGeoId", entry.getString("countryGeoId"));
				postalContactMech.put("stateProvinceGeoId", entry.getString("stateProvinceGeoId"));
				postalContactMech.store();
			} else if (UtilValidate.isNotEmpty(entry.getString("address1"))
					|| UtilValidate.isNotEmpty(entry.getString("city"))) {
				// associate this as the GENERAL_LOCATION and BILLING_LOCATION
				GenericValue contactMech = delegator.makeValue("ContactMech", UtilMisc.toMap("contactMechId",
						delegator.getNextSeqId("ContactMech"), "contactMechTypeId", "POSTAL_ADDRESS"));
				String postalAddressContactMechId = contactMech.getString("contactMechId");
				GenericValue mainPostalAddress = UtilImport.makePostalAddress(contactMech, fullName, "", "",
						entry.getString("attnName"), entry.getString("address1"), entry.getString("address2"),
						entry.getString("city"), entry.getString("stateProvinceGeoId"), entry.getString("postalCode"),
						entry.getString("postalCodeExt"), entry.getString("countryGeoId"), delegator);
				toBeStored.add(contactMech);
				toBeStored.add(mainPostalAddress);

				toBeStored.add(UtilImport.makeContactMechPurpose("GENERAL_LOCATION", mainPostalAddress, partyId,
						importTimestamp, delegator));
				toBeStored.add(UtilImport.makeContactMechPurpose("BILLING_LOCATION", mainPostalAddress, partyId,
						importTimestamp, delegator));
				toBeStored.add(UtilImport.makeContactMechPurpose("PRIMARY_LOCATION", mainPostalAddress, partyId,
						importTimestamp, delegator));
				toBeStored.add(delegator.makeValue("PartyContactMech",
						UtilMisc.toMap("contactMechId", postalAddressContactMechId, "partyId", partyId, "fromDate",
								importTimestamp, "allowSolicitation", "Y")));
				partySupplementalData.set("primaryPostalAddressId", postalAddressContactMechId);
			}

			// update Shipping Postal Address
			GenericValue shipPostalContactMech = EntityUtil.getFirst(delegator.findByAnd("PartyContactMechPurpose",
					UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "SHIPPING_LOCATION"), null, false));
			if (UtilValidate.isNotEmpty(shipPostalContactMech)) {
				String contactMechId = shipPostalContactMech.getString("contactMechId");
				GenericValue shipContactMech = delegator.findOne("PostalAddress", false,
						UtilMisc.toMap("contactMechId", contactMechId));
				shipContactMech.put("toName", fullName);
				shipContactMech.put("attnName", entry.getString("shipToAttnName"));
				shipContactMech.put("address1", entry.getString("shipToAddress1"));
				shipContactMech.put("address2", entry.getString("shipToAddress2"));
				shipContactMech.put("city", entry.getString("shipToCity"));
				shipContactMech.put("postalCode", entry.getString("shipToPostalCode"));
				shipContactMech.put("postalCodeExt", entry.getString("shipToPostalCodeExt"));
				shipContactMech.put("countryGeoId", entry.getString("shipToCountryGeoId"));
				shipContactMech.put("stateProvinceGeoId", entry.getString("shipToStateProvinceGeoId"));
				shipContactMech.store();
			}
			// update noteleadId
			if (!UtilValidate.isEmpty(entry.getString("note"))) {
				// make the party note
				GenericValue findNote = EntityUtil
						.getFirst(delegator.findByAnd("PartyNote", UtilMisc.toMap("partyId", partyId), null, false));
				if (UtilValidate.isNotEmpty(findNote)) {
					GenericValue noteData = delegator.findOne("NoteData", false,
							UtilMisc.toMap("noteId", findNote.getString("noteId")));
					noteData.put("noteInfo", entry.getString("note"));
					noteData.store();
				} else {
					GenericValue noteData = delegator.makeValue("NoteData",
							UtilMisc.toMap("noteId", delegator.getNextSeqId("NoteData"), "noteInfo",
									entry.getString("note"), "noteDateTime", importTimestamp));
					toBeStored.add(noteData);
					toBeStored.add(delegator.makeValue("PartyNote",
							UtilMisc.toMap("noteId", noteData.get("noteId"), "partyId", partyId)));
				}
			}

			if (!UtilValidate.isEmpty(entry.getString("source"))) {
				List conditionList = FastList.newInstance();
				
				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				//conditionList.add(EntityCondition.makeCondition("dataSourceId", EntityOperator.EQUALS, entry.getString("source")));
				conditionList.add(EntityUtil.getFilterByDateExpr());
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> dataSourceList = delegator.findList("PartyDataSource", mainConditons, null, null, null, false);
				if (UtilValidate.isNotEmpty(dataSourceList)) {
					Set<String> sources = dataSourceList.stream().map(e->e.getString("dataSourceId")).collect(Collectors.toSet());
					if (!sources.contains(entry.getString("source"))) {
						dispatcher.runSync("createPartyDataSource", UtilMisc.toMap("partyId", partyId , "dataSourceId", entry.getString("source"), "userLogin", userLogin, "fromDate", UtilDateTime.nowTimestamp()));
					}
					for (GenericValue dataSource : dataSourceList) {
						if (!dataSource.getString("dataSourceId").equals(entry.getString("source"))) {
							dataSource.put("thruDate", UtilDateTime.nowTimestamp());
							toBeStored.add(dataSource);
						}
					}
				}
				
				partySupplementalData.put("source", entry.getString("source"));
			}
			
			if (UtilValidate.isNotEmpty(entry.getString("segmentation"))) {
				GenericValue customField = EntityQuery.use(delegator).from("CustomField").where("customFieldId",entry.getString("segmentation")).queryFirst();
				if (UtilValidate.isNotEmpty(customField)) {
					GenericValue customFieldPartyClassification = delegator.makeValue("CustomFieldPartyClassification", UtilMisc.toMap("groupId",
							customField.getString("groupId"), "customFieldId",customField.getString("customFieldId"),"partyId",partyId,"groupActualValue","Y",
							"inceptionDate",UtilDateTime.nowTimestamp()));
					toBeStored.add(customFieldPartyClassification);
				}
			}

			prepareSupplementalData(partySupplementalData, entry);

			toBeStored.add(partySupplementalData);

			entry.put("primaryPartyId", partyId);
			toBeStored.add(entry);

			toBeStored.add(userLogin);

			return toBeStored;

		}
		
		/***********************/
		/** Import Party data **/
		/***********************/
		// create the company with the roles

		/*String seqName = "LEAD-" + entry.get("source").toString();
		String prefix = entry.get("source").toString();
		partyId = DataUtil.getLeadId(prefix, delegator.getNextSeqId(seqName, 0));*/
		
		partyId = delegator.getNextSeqId("Party");
		
		String leadPartyId = entry.getString("leadId");
		if (UtilValidate.isEmpty(leadPartyId)) {
			leadPartyId = partyId;
		}
		
		List<GenericValue> partyValues = FastList.newInstance();
		
		partyValues.add(delegator.makeValue("Party", UtilMisc.toMap("partyId", partyId, "roleTypeId", "LEAD", "partyTypeId", "PARTY_GROUP", "externalId", leadPartyId, "createdDate",UtilDateTime.nowTimestamp(), "statusId", "LEAD_UNIVERSE"
				, "description", entry.getString("description"), "preferredCurrencyUomId", entry.getString("currencyUomId"), "timeZoneDesc", entry.getString("timeZoneId"), "createdByUserLogin", userLogin.getString("userLoginId"))));
		partyValues.add(delegator.makeValue("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "LEAD")));
		partyValues.add(delegator.makeValue("PartyIdentification", UtilMisc.toMap("partyId", partyId, "partyIdentificationTypeId", "EXT_PARTY_ID", "idValue", leadPartyId)));
		toBeStored.addAll(partyValues);
		//toBeStored.addAll(UtilImport.makePartyWithRolesExt(partyId, "PARTY_GROUP", leadPartyId, UtilMisc.toList("LEAD"), delegator));
		
		GenericValue partyGroup = delegator.makeValue("PartyGroup",
				UtilMisc.toMap("partyId", partyId, "groupName", fullName, "groupNameLocal", entry.getString("groupNameLocal")));
		toBeStored.add(partyGroup);

		Boolean personResponsibleForCreate = true;
		if (UtilValidate.isNotEmpty(entry.getString("primaryPhoneNumber"))
				&& UtilValidate.isNotEmpty(entry.getString("secondaryPhoneNumber"))) {
			Map<String, Object> dndStatusPrimaryPhoneMp = DataUtil.getDndStatus(delegator,
					entry.getString("primaryPhoneNumber"));
			Map<String, Object> dndStatusSecondaryPhoneMp = DataUtil.getDndStatus(delegator,
					entry.getString("secondaryPhoneNumber"));
			if ("Y".equals(dndStatusPrimaryPhoneMp.get("dndStatus"))
					&& "Y".equals(dndStatusSecondaryPhoneMp.get("dndStatus"))) {
				personResponsibleForCreate = false;
			}
		}
		if (personResponsibleForCreate) {
			String personResponsible = entry.getString("personResponsible");
			if (UtilValidate.isEmpty(personResponsible)) {
				personResponsible = "admin";
			}
			String responsibleRoleType = PartyHelper.getPartyRoleTypeId(personResponsible, delegator);
			if (UtilValidate.isEmpty(responsibleRoleType)) {
				responsibleRoleType = "EMPLOYEE";
			}
			Map<String, Object> partyRelationship = UtilMisc.toMap("partyIdTo", personResponsible, "roleTypeIdTo", responsibleRoleType,
					"partyIdFrom", partyId, "roleTypeIdFrom", "LEAD", "partyRelationshipTypeId", "RESPONSIBLE_FOR",
					"fromDate", importTimestamp);
			partyRelationship.put("securityGroupId", "ACCOUNT_OWNER");
			toBeStored.add(delegator.makeValue("PartyRelationship", partyRelationship));
		}

		//String primaryPartyName = org.ofbiz.party.party.PartyHelper.getPartyName(company);
		Debug.logInfo("Creating PartyGroup [" + partyId + "] for Lead [" + entry.get("leadId") + "].", module);

		GenericValue partySupplementalData = delegator.makeValue("PartySupplementalData",
				UtilMisc.toMap("partyId", partyId, "companyName", entry.getString("companyName"), "numberEmployees", entry.getLong("noOfEmployees")));
		partySupplementalData.put("annualRevenue", entry.get("annualRevenue"));
		
		if (UtilValidate.isNotEmpty(entry.getString("address1")) || UtilValidate.isNotEmpty(entry.getString("city"))) {
			// associate this as the GENERAL_LOCATION and BILLING_LOCATION
			GenericValue contactMech = delegator.makeValue("ContactMech", UtilMisc.toMap("contactMechId",
					delegator.getNextSeqId("ContactMech"), "contactMechTypeId", "POSTAL_ADDRESS"));
			String postalAddressContactMechId = contactMech.getString("contactMechId");
			GenericValue mainPostalAddress = UtilImport.makePostalAddress(contactMech, fullName, "", "",
					entry.getString("attnName"), entry.getString("address1"), entry.getString("address2"),
					entry.getString("city"), entry.getString("stateProvinceGeoId"), entry.getString("postalCode"),
					entry.getString("postalCodeExt"), entry.getString("countryGeoId"), delegator);
			toBeStored.add(contactMech);
			toBeStored.add(mainPostalAddress);

			toBeStored.add(UtilImport.makeContactMechPurpose("GENERAL_LOCATION", mainPostalAddress, partyId,
					importTimestamp, delegator));
			toBeStored.add(UtilImport.makeContactMechPurpose("BILLING_LOCATION", mainPostalAddress, partyId,
					importTimestamp, delegator));
			toBeStored.add(UtilImport.makeContactMechPurpose("PRIMARY_LOCATION", mainPostalAddress, partyId,
					importTimestamp, delegator));
			toBeStored.add(
					delegator.makeValue("PartyContactMech", UtilMisc.toMap("contactMechId", postalAddressContactMechId,
							"partyId", partyId, "fromDate", importTimestamp, "allowSolicitation", "Y")));
			partySupplementalData.set("primaryPostalAddressId", postalAddressContactMechId);
		}

		if (!UtilValidate.isEmpty(entry.getString("primaryPhoneNumber"))) {
			// associate this as PRIMARY_PHONE
			GenericValue contactMech = delegator.makeValue("ContactMech", UtilMisc.toMap("contactMechId",
					delegator.getNextSeqId("ContactMech"), "contactMechTypeId", "TELECOM_NUMBER"));
			String telecomContactMechId = contactMech.getString("contactMechId");

			// check and store dnd status in primay phone number
			Map<String, Object> dndStatusMp = DataUtil.getDndStatus(delegator, entry.getString("primaryPhoneNumber"));

			GenericValue primaryNumber = UtilImport.makeTelecomNumber(contactMech,
					entry.getString("primaryPhoneCountryCode"), entry.getString("primaryPhoneAreaCode"),
					entry.getString("primaryPhoneNumber"), delegator);
			primaryNumber.put("askForName", entry.getString("keyContactPerson1"));
			primaryNumber.put("dndStatus", dndStatusMp.get("dndStatus"));
			toBeStored.add(contactMech);
			toBeStored.add(primaryNumber);

			String dndIndicator = (String) dndStatusMp.get("dndIndicator");
			String dndSeqId = (String) dndStatusMp.get("dndSeqId");
			if (UtilValidate.isNotEmpty(dndSeqId) && UtilValidate.isNotEmpty(dndIndicator)) {
				toBeStored.add(DataUtil.makeDndAuditLogDetails(dndSeqId, partyId, "CREATE",
						entry.getString("primaryPhoneNumber"), dndIndicator, importTimestamp, delegator));
			}

			toBeStored.add(UtilImport.makeContactMechPurpose("PRIMARY_PHONE", primaryNumber, partyId, importTimestamp,
					delegator));
			toBeStored.add(delegator.makeValue("PartyContactMech",
					UtilMisc.toMap("contactMechId", telecomContactMechId, "partyId", partyId, "fromDate",
							importTimestamp, "extension", entry.getString("primaryPhoneExtension"), "allowSolicitation",
							dndStatusMp.get("solicitationStatus"))));
			partySupplementalData.set("primaryTelecomNumberId", telecomContactMechId);
		}

		if (!UtilValidate.isEmpty(entry.getString("secondaryPhoneNumber"))) {
			// this one has no contactmech purpose type
			GenericValue contactMech = delegator.makeValue("ContactMech", UtilMisc.toMap("contactMechId",
					delegator.getNextSeqId("ContactMech"), "contactMechTypeId", "TELECOM_NUMBER"));

			// check and store dnd status in secondary phone number
			Map<String, Object> dndStatusMp = DataUtil.getDndStatus(delegator, entry.getString("secondaryPhoneNumber"));

			GenericValue secondaryNumber = UtilImport.makeTelecomNumber(contactMech,
					entry.getString("secondaryPhoneCountryCode"), entry.getString("secondaryPhoneAreaCode"),
					entry.getString("secondaryPhoneNumber"), delegator);
			secondaryNumber.put("askForName", entry.getString("keyContactPerson2"));
			secondaryNumber.put("dndStatus", dndStatusMp.get("dndStatus"));

			toBeStored.add(contactMech);
			toBeStored.add(secondaryNumber);

			String dndIndicator = (String) dndStatusMp.get("dndIndicator");
			String dndSeqId = (String) dndStatusMp.get("dndSeqId");
			if (UtilValidate.isNotEmpty(dndSeqId) && UtilValidate.isNotEmpty(dndIndicator)) {
				toBeStored.add(DataUtil.makeDndAuditLogDetails(dndSeqId, partyId, "CREATE",
						entry.getString("secondaryPhoneNumber"), dndIndicator, importTimestamp, delegator));
			}

			toBeStored.add(UtilImport.makeContactMechPurpose("PHONE_WORK_SEC", secondaryNumber, partyId,
					importTimestamp, delegator));
			toBeStored.add(delegator.makeValue("PartyContactMech",
					UtilMisc.toMap("contactMechId", contactMech.get("contactMechId"), "partyId", partyId, "fromDate",
							importTimestamp, "extension", entry.getString("secondaryPhoneExtension"),
							"allowSolicitation", dndStatusMp.get("solicitationStatus"))));
		}

		if (!UtilValidate.isEmpty(entry.getString("faxNumber"))) {
			// associate this as FAX_NUMBER
			GenericValue contactMech = delegator.makeValue("ContactMech", UtilMisc.toMap("contactMechId",
					delegator.getNextSeqId("ContactMech"), "contactMechTypeId", "TELECOM_NUMBER"));
			GenericValue faxNumber = UtilImport.makeTelecomNumber(contactMech, entry.getString("faxCountryCode"),
					entry.getString("faxAreaCode"), entry.getString("faxNumber"), delegator);
			toBeStored.add(contactMech);
			toBeStored.add(faxNumber);

			toBeStored.add(
					UtilImport.makeContactMechPurpose("FAX_NUMBER", faxNumber, partyId, importTimestamp, delegator));
			toBeStored.add(delegator.makeValue("PartyContactMech",
					UtilMisc.toMap("contactMechId", contactMech.get("contactMechId"), "partyId", partyId, "fromDate",
							importTimestamp, "allowSolicitation", "Y")));
		}

		if (!UtilValidate.isEmpty(entry.getString("didNumber"))) {
			// associate this as PHONE_DID
			GenericValue contactMech = delegator.makeValue("ContactMech", UtilMisc.toMap("contactMechId",
					delegator.getNextSeqId("ContactMech"), "contactMechTypeId", "TELECOM_NUMBER"));
			GenericValue didNumber = UtilImport.makeTelecomNumber(contactMech, entry.getString("didCountryCode"),
					entry.getString("didAreaCode"), entry.getString("didNumber"), delegator);
			toBeStored.add(contactMech);
			toBeStored.add(didNumber);

			toBeStored.add(
					UtilImport.makeContactMechPurpose("PHONE_DID", didNumber, partyId, importTimestamp, delegator));
			toBeStored.add(delegator.makeValue("PartyContactMech",
					UtilMisc.toMap("contactMechId", contactMech.get("contactMechId"), "partyId", partyId, "fromDate",
							importTimestamp, "extension", entry.getString("didExtension"), "allowSolicitation", "Y")));
		}

		if (!UtilValidate.isEmpty(entry.getString("emailAddress"))) {
			// make the email address
			GenericValue emailContactMech = delegator.makeValue("ContactMech",
					UtilMisc.toMap("contactMechId", delegator.getNextSeqId("ContactMech"), "contactMechTypeId",
							"EMAIL_ADDRESS", "infoString", entry.getString("emailAddress")));
			String emailContactMechId = emailContactMech.getString("contactMechId");
			toBeStored.add(emailContactMech);

			toBeStored.add(delegator.makeValue("PartyContactMech", UtilMisc.toMap("contactMechId", emailContactMechId,
					"partyId", partyId, "fromDate", importTimestamp, "allowSolicitation", "Y")));
			toBeStored.add(UtilImport.makeContactMechPurpose("PRIMARY_EMAIL", emailContactMech, partyId,
					importTimestamp, delegator));
			partySupplementalData.set("primaryEmailId", emailContactMechId);
		}

		if (!UtilValidate.isEmpty(entry.getString("webAddress"))) {
			// make the web address
			GenericValue webContactMech = delegator.makeValue("ContactMech",
					UtilMisc.toMap("contactMechId", delegator.getNextSeqId("ContactMech"), "contactMechTypeId",
							"WEB_ADDRESS", "infoString", entry.getString("webAddress")));
			toBeStored.add(webContactMech);

			toBeStored.add(delegator.makeValue("PartyContactMech",
					UtilMisc.toMap("contactMechId", webContactMech.get("contactMechId"), "partyId", partyId, "fromDate",
							importTimestamp, "allowSolicitation", "Y")));
			toBeStored.add(UtilImport.makeContactMechPurpose("PRIMARY_WEB_URL", webContactMech, partyId,
					importTimestamp, delegator));
		}
		
		// Social media [start]
		
		if (!UtilValidate.isEmpty(entry.getString("facebookContact"))) {
			GenericValue socialMediaContactMech = delegator.makeValue("ContactMech",
					UtilMisc.toMap("contactMechId", delegator.getNextSeqId("ContactMech"), "contactMechTypeId",
							"SOCIAL_MEDIA_TYPE", "infoString", entry.getString("facebookContact")));
			String socalMediaContactMechId = socialMediaContactMech.getString("contactMechId");
			toBeStored.add(socialMediaContactMech);

			toBeStored.add(delegator.makeValue("PartyContactMech", UtilMisc.toMap("contactMechId", socalMediaContactMechId,
					"partyId", partyId, "fromDate", importTimestamp, "allowSolicitation", "Y")));
			toBeStored.add(UtilImport.makeContactMechPurpose("SOCIAL_FACEBOOK", socialMediaContactMech, partyId,
					importTimestamp, delegator));
			//partySupplementalData.set("primaryEmailId", socalMediaContactMechId);
		}
		
		if (!UtilValidate.isEmpty(entry.getString("googleContact"))) {
			GenericValue socialMediaContactMech = delegator.makeValue("ContactMech",
					UtilMisc.toMap("contactMechId", delegator.getNextSeqId("ContactMech"), "contactMechTypeId",
							"SOCIAL_MEDIA_TYPE", "infoString", entry.getString("googleContact")));
			String socalMediaContactMechId = socialMediaContactMech.getString("contactMechId");
			toBeStored.add(socialMediaContactMech);

			toBeStored.add(delegator.makeValue("PartyContactMech", UtilMisc.toMap("contactMechId", socalMediaContactMechId,
					"partyId", partyId, "fromDate", importTimestamp, "allowSolicitation", "Y")));
			toBeStored.add(UtilImport.makeContactMechPurpose("SOCIAL_GOOGLE", socialMediaContactMech, partyId,
					importTimestamp, delegator));
			//partySupplementalData.set("primaryEmailId", socalMediaContactMechId);
		}
		
		if (!UtilValidate.isEmpty(entry.getString("instagramContact"))) {
			GenericValue socialMediaContactMech = delegator.makeValue("ContactMech",
					UtilMisc.toMap("contactMechId", delegator.getNextSeqId("ContactMech"), "contactMechTypeId",
							"SOCIAL_MEDIA_TYPE", "infoString", entry.getString("instagramContact")));
			String socalMediaContactMechId = socialMediaContactMech.getString("contactMechId");
			toBeStored.add(socialMediaContactMech);

			toBeStored.add(delegator.makeValue("PartyContactMech", UtilMisc.toMap("contactMechId", socalMediaContactMechId,
					"partyId", partyId, "fromDate", importTimestamp, "allowSolicitation", "Y")));
			toBeStored.add(UtilImport.makeContactMechPurpose("SOCIAL_INSTAGRAM", socialMediaContactMech, partyId,
					importTimestamp, delegator));
			//partySupplementalData.set("primaryEmailId", socalMediaContactMechId);
		}
		
		if (!UtilValidate.isEmpty(entry.getString("linkedInContact"))) {
			GenericValue socialMediaContactMech = delegator.makeValue("ContactMech",
					UtilMisc.toMap("contactMechId", delegator.getNextSeqId("ContactMech"), "contactMechTypeId",
							"SOCIAL_MEDIA_TYPE", "infoString", entry.getString("linkedInContact")));
			String socalMediaContactMechId = socialMediaContactMech.getString("contactMechId");
			toBeStored.add(socialMediaContactMech);

			toBeStored.add(delegator.makeValue("PartyContactMech", UtilMisc.toMap("contactMechId", socalMediaContactMechId,
					"partyId", partyId, "fromDate", importTimestamp, "allowSolicitation", "Y")));
			toBeStored.add(UtilImport.makeContactMechPurpose("SOCIAL_LINKEDIN", socialMediaContactMech, partyId,
					importTimestamp, delegator));
			//partySupplementalData.set("primaryEmailId", socalMediaContactMechId);
		}
		
		if (!UtilValidate.isEmpty(entry.getString("twitterContact"))) {
			GenericValue socialMediaContactMech = delegator.makeValue("ContactMech",
					UtilMisc.toMap("contactMechId", delegator.getNextSeqId("ContactMech"), "contactMechTypeId",
							"SOCIAL_MEDIA_TYPE", "infoString", entry.getString("twitterContact")));
			String socalMediaContactMechId = socialMediaContactMech.getString("contactMechId");
			toBeStored.add(socialMediaContactMech);

			toBeStored.add(delegator.makeValue("PartyContactMech", UtilMisc.toMap("contactMechId", socalMediaContactMechId,
					"partyId", partyId, "fromDate", importTimestamp, "allowSolicitation", "Y")));
			toBeStored.add(UtilImport.makeContactMechPurpose("SOCIAL_TWITTER", socialMediaContactMech, partyId,
					importTimestamp, delegator));
			//partySupplementalData.set("primaryEmailId", socalMediaContactMechId);
		}
		
		if (!UtilValidate.isEmpty(entry.getString("youtubeContact"))) {
			GenericValue socialMediaContactMech = delegator.makeValue("ContactMech",
					UtilMisc.toMap("contactMechId", delegator.getNextSeqId("ContactMech"), "contactMechTypeId",
							"SOCIAL_MEDIA_TYPE", "infoString", entry.getString("youtubeContact")));
			String socalMediaContactMechId = socialMediaContactMech.getString("contactMechId");
			toBeStored.add(socialMediaContactMech);

			toBeStored.add(delegator.makeValue("PartyContactMech", UtilMisc.toMap("contactMechId", socalMediaContactMechId,
					"partyId", partyId, "fromDate", importTimestamp, "allowSolicitation", "Y")));
			toBeStored.add(UtilImport.makeContactMechPurpose("SOCIAL_YOUTUBE", socialMediaContactMech, partyId,
					importTimestamp, delegator));
			//partySupplementalData.set("primaryEmailId", socalMediaContactMechId);
		}
		
		// Social media [end]

		if (!UtilValidate.isEmpty(entry.getString("note"))) {
			// make the party note
			GenericValue noteData = delegator.makeValue("NoteData",
					UtilMisc.toMap("noteId", delegator.getNextSeqId("NoteData"), "noteInfo", entry.getString("note"),
							"noteDateTime", importTimestamp));
			toBeStored.add(noteData);
			toBeStored.add(delegator.makeValue("PartyNote",
					UtilMisc.toMap("noteId", noteData.get("noteId"), "partyId", partyId)));
		}

		if (!UtilValidate.isEmpty(entry.getString("source"))) {
			toBeStored.add(delegator.makeValue("PartyDataSource", UtilMisc.toMap("partyId", partyId,
					"dataSourceId", entry.getString("source"), "fromDate", UtilDateTime.nowTimestamp())));
			partySupplementalData.put("source", entry.getString("source"));
		}
		
		if (UtilValidate.isNotEmpty(entry.getString("segmentation"))) {
			GenericValue customField = EntityQuery.use(delegator).from("CustomField").where("customFieldId",entry.getString("segmentation")).queryFirst();
			if (UtilValidate.isNotEmpty(customField)) {
				GenericValue customFieldPartyClassification = delegator.makeValue("CustomFieldPartyClassification", UtilMisc.toMap("groupId",
						customField.getString("groupId"), "customFieldId",customField.getString("customFieldId"),"partyId",partyId,"groupActualValue","Y",
						"inceptionDate",UtilDateTime.nowTimestamp()));
				toBeStored.add(customFieldPartyClassification);
			}
		}

		prepareSupplementalData(partySupplementalData, entry);

		entry.put("primaryPartyId", partyId);
		toBeStored.add(entry);

		toBeStored.add(partySupplementalData);

		return toBeStored;
	}

	private static void prepareSupplementalData(GenericValue partySupplementalData, GenericValue entry) {
		partySupplementalData.put("partyFirstName", entry.getString("firstName"));
		partySupplementalData.put("partyLastName", entry.getString("lastName"));

		partySupplementalData.put("companyName", entry.get("companyName"));
		partySupplementalData.put("dateOfIncorporation", entry.get("dateOfIncorporation"));
		partySupplementalData.put("constitution", entry.get("constitution"));
		partySupplementalData.put("industryCat", entry.get("industryCat"));
		partySupplementalData.put("industry", entry.get("industry"));
		partySupplementalData.put("industryEnumId", entry.get("industry"));
		partySupplementalData.put("customerTradingType", entry.get("customerTradingType"));
		partySupplementalData.put("keyContactPerson1", entry.get("keyContactPerson1"));
		partySupplementalData.put("keyContactPerson2", entry.get("keyContactPerson2"));
		partySupplementalData.put("leadAssignTo", entry.get("leadAssignTo"));
		partySupplementalData.put("leadAssignBy", entry.get("leadAssignBy"));
		partySupplementalData.put("segment", entry.get("segment"));
		partySupplementalData.put("title", entry.get("title"));
		partySupplementalData.put("noOfEmployees", entry.get("noOfEmployees"));
		partySupplementalData.put("errorCodes", entry.get("errorCodes"));
		partySupplementalData.put("approvedByUserLoginId", entry.get("approvedByUserLoginId"));
		partySupplementalData.put("rejectedByUserLoginId", entry.get("rejectedByUserLoginId"));
		partySupplementalData.put("uploadedByUserLoginId", entry.get("uploadedByUserLoginId"));

		partySupplementalData.put("address1", entry.get("address1"));
		partySupplementalData.put("address2", entry.get("address2"));
		partySupplementalData.put("city", entry.get("city"));
		partySupplementalData.put("stateProvinceGeoId", entry.get("stateProvinceGeoId"));
		partySupplementalData.put("postalCode", entry.get("postalCode"));
		partySupplementalData.put("primaryPhoneCountryCode", entry.get("primaryPhoneCountryCode"));
		partySupplementalData.put("primaryPhoneNumber", entry.get("primaryPhoneNumber"));
		partySupplementalData.put("secondaryPhoneCountryCode", entry.get("secondaryPhoneCountryCode"));
		partySupplementalData.put("secondaryPhoneNumber", entry.get("secondaryPhoneNumber"));
		partySupplementalData.put("note", entry.get("note"));
		partySupplementalData.put("emailAddress", entry.get("emailAddress"));
		partySupplementalData.put("webAddress", entry.get("webAddress"));
		partySupplementalData.put("designation", entry.get("designation"));
		partySupplementalData.put("ownershipEnumId", entry.get("ownershipEnumId"));

		partySupplementalData.put("source", entry.get("source"));
		partySupplementalData.put("leadScore", entry.get("leadScore"));
		partySupplementalData.put("virtualTeamId", entry.get("virtualTeamId"));
	}

	public static List<String> getDupPartyList(GenericValue entry, Delegator delegator) {

		Map<String, Object> outMap = new HashMap<String, Object>();
		String fullName = UtilImport.getFullName(entry.getString("firstName"), entry.getString("lastName"));

		String groupName = fullName;
		String zipCode = (String) entry.get("postalCode");
		String address = (String) entry.get("address1");
		String address1 = StringUtils.defaultIfEmpty(address, " ");

		String country = (String) entry.get("countryGeoId");

		String phone = (String) entry.get("primaryPhoneAreaCode") + (String) entry.get("primaryPhoneNumber");

		List<String> partyIdsList = FastList.newInstance();

		List<String> matchPartyList = FastList.newInstance();

		if (UtilValidate.isNotEmpty(groupName) && UtilValidate.isNotEmpty(zipCode) && UtilValidate.isNotEmpty(address1)
				&& UtilValidate.isNotEmpty(country)) {

			String SqlQuery = "";

			String postal = zipCode;

			SqlQuery = "select  a.party_id,a.GROUP_NAME from party_group a, party_contact_mech c,  postal_address d where a.PARTY_ID=c.PARTY_ID and  d.CONTACT_MECH_ID=c.CONTACT_MECH_ID and  a.group_name ='"
					+ groupName + "' and  d.postal_code = '" + postal + "'";

			if (UtilValidate.isNotEmpty(SqlQuery)) {
				
				List<String> PartySQlList = UtilImport.runSqlQuery(SqlQuery, delegator);

				if (UtilValidate.isNotEmpty(PartySQlList)) {
					for (String partyId : PartySQlList) {
						partyIdsList.add(partyId);
					}
				}

			}
			try {

				for (String partyId : partyIdsList) {

					GenericValue party = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);

					if (party != null) {

						String postalContact = getPrimaryContact(partyId, "GENERAL_LOCATION", delegator);

						if (postalContact != null) {
							GenericValue postalAddress = delegator.findOne("PostalAddress",
									UtilMisc.toMap("contactMechId", postalContact), false);

							if (postalAddress != null) {
								String address1s = postalAddress.getString("address1");
								String stateProvinceGeoIds = postalAddress.getString("stateProvinceGeoId");
								String countryGeoIds = postalAddress.getString("countryGeoId");
								if (UtilValidate.isNotEmpty(address1) && address1.equalsIgnoreCase(address1s)
										&& country.equals(countryGeoIds)) {
									matchPartyList.add(partyId);
								}
							}
						}
					}
				}

			} catch (GenericEntityException ex) {

			}

		}

		return matchPartyList;

	}
	
	public static String getPrimaryContact(String partyId, String ContactMecPurpose, Delegator delegator) {
		String contactMechId = null;

		try {

			List<EntityCondition> conditon = UtilMisc.<EntityCondition>toList(
					EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), EntityCondition
							.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, ContactMecPurpose));

			EntityCondition findConditions1 = EntityCondition.makeCondition(conditon, EntityOperator.AND);

			EntityFindOptions findOpt1 = new EntityFindOptions();
			findOpt1.setDistinct(true);

			List<String> orderBy1 = FastList.newInstance();
			orderBy1.add("fromDate DESC");

			List<GenericValue> pcpurpose = delegator.findList("PartyContactMechPurpose", findConditions1, null,
					orderBy1, findOpt1, false);
			pcpurpose = EntityUtil.filterByDate(pcpurpose);
			if (UtilValidate.isNotEmpty(pcpurpose)) {
				GenericValue phoneContact = EntityUtil.getFirst(pcpurpose);
				pcpurpose = delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId",
						phoneContact.getString("partyId"), "contactMechId", phoneContact.getString("contactMechId")),
						null, false);
			}

			pcpurpose = EntityUtil.filterByDate(pcpurpose, true);

			GenericValue partyContactMech = EntityUtil.getFirst(pcpurpose);

			if (partyContactMech != null) {
				contactMechId = partyContactMech.getString("contactMechId");
			}

		} catch (GenericEntityException ex) {

		}

		return contactMechId;

	}
	
}
