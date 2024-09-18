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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fio.dataimport.util.DataUtil;
import org.groupfio.common.portal.util.UtilContactMech;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;

import javolution.util.FastList;


/**
 * maps DataImportContact into a set of opentaps entities that describes the Contact
 * TODO: break this up big method into several logical steps, each implemented in class methods.
 * This will allow for much easier re-use for custom imports.
 */
public class ContactDecoder implements ImportDecoder {
	
    private static final String MODULE = ContactDecoder.class.getName();
    
    protected String organizationPartyId;
    protected GenericValue userLogin;

    /**
     * Creates a contact decoder from an input context.
     * This will automatically extract the class variables it needs and
     * validate for the existence of GL accounts and CRMSFA roles.
     * If there is a problem, a GeneralException is thrown.
     */
    public ContactDecoder(Map<String, ?> context) throws GeneralException {
        this.organizationPartyId = (String) context.get("organizationPartyId");
        this.userLogin = (GenericValue) context.get("userLogin");
        
        Delegator delegator = userLogin.getDelegator();
    }

    public static List<String> TEAM_MEMBER_ROLES = UtilMisc.toList("EMPLOYEE", "ACCOUNT_REP", "CUST_SERVICE_REP");
    
    public List<GenericValue> decode(GenericValue entry, Timestamp importTimestamp, Delegator delegator, LocalDispatcher dispatcher, Object... args) throws Exception {
        
    	Map<String, Object> callCtxt = new HashMap<String, Object>();
		Map<String, Object> callResult = new HashMap<String, Object>();
    	
    	String fullName = UtilImport.getFullName(entry.getString("firstName"), entry.getString("lastName"));
		
		List<GenericValue> toBeStored = FastList.newInstance();

		Debug.logInfo("Now processing Contact name [" + fullName + "]", MODULE);

		String partyId = null;
		List<EntityCondition> conditions = new ArrayList<EntityCondition>();
		conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
				EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, entry.getString("contactId")),
				EntityCondition.makeCondition("externalId", EntityOperator.EQUALS, entry.getString("contactId"))
				));
    	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
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

			party.put("description", entry.getString("description"));
			party.put("timeZoneDesc", entry.getString("timeZoneId"));
			
			toBeStored.add(party);
			
			// update basic details
			
			GenericValue person = EntityUtil.getFirst(
					delegator.findByAnd("Person", UtilMisc.toMap("partyId", partyId), null, false));
			if (UtilValidate.isNotEmpty(person)) {
				person.put("firstName", entry.getString("firstName"));
				person.put("lastName", entry.getString("lastName"));
				person.put("middleName", entry.getString("middleName"));
				person.put("gender", entry.getString("gender"));
				person.put("personalTitle", entry.getString("personalTitle"));
				person.put("occupation", entry.getString("occupation"));
				person.put("maritalStatus", entry.getString("maritalStatus"));
				person.put("designation", entry.getString("designation"));
				if (UtilValidate.isNotEmpty(entry.getString("birthDate"))) {
					person.put("birthDate", org.fio.homeapps.util.UtilDateTime.toSqlDate(entry.getString("birthDate")));
				}
				
				toBeStored.add(person);
			}
			
			// update phone contact
			Map<String, Object> phoneDetail = UtilContactMech.getPartyPhoneDetail(delegator, partyId, "PRIMARY_PHONE");
			if (UtilValidate.isNotEmpty(phoneDetail)) {
				String contactMechId = (String) phoneDetail.get("contactMechId");

				// check and store dnd status in primay phone number
				Map<String, Object> dndStatusMp = DataUtil.getDndStatus(delegator,
						entry.getString("primaryPhoneNumber"));

				GenericValue phoneContactMech = delegator.findOne("TelecomNumber", false,
						UtilMisc.toMap("contactMechId", contactMechId));
				// phoneContactMech.put("contactMechId", contactMechId);
				phoneContactMech.put("countryCode", entry.getString("primaryPhoneCountryCode"));
				phoneContactMech.put("areaCode", entry.getString("primaryPhoneAreaCode"));
				phoneContactMech.put("contactNumber", entry.getString("primaryPhoneNumber"));
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

			phoneDetail = UtilContactMech.getPartyPhoneDetail(delegator, partyId, "PHONE_WORK_SEC");
			if (UtilValidate.isNotEmpty(phoneDetail)) {
				String contactMechId = (String) phoneDetail.get("contactMechId");

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
			Map<String, Object> emailDetail = UtilContactMech.getPartyEmailDetail(delegator, partyId, "PRIMARY_EMAIL");
			if (UtilValidate.isNotEmpty(emailDetail)) {
				GenericValue emailContactMech = delegator.findOne("ContactMech", false,
						UtilMisc.toMap("contactMechId", emailDetail.get("contactMechId")));
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
			Map<String, Object> webDetail = UtilContactMech.getPartyEmailDetail(delegator, partyId, "PRIMARY_WEB_URL");
			if (UtilValidate.isNotEmpty(webDetail)) {
				GenericValue webContactMech = delegator.findOne("ContactMech", false,
						UtilMisc.toMap("contactMechId", webDetail.get("contactMechId")));
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
			phoneDetail = UtilContactMech.getPartyPhoneDetail(delegator, partyId, "FAX_NUMBER");
			if (UtilValidate.isNotEmpty(phoneDetail)) {
				String contactMechId = (String) phoneDetail.get("contactMechId");
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

			// update Postal Address
			GenericValue postalAddress = UtilContactMech.getPartyPostal(delegator, partyId, "GENERAL_LOCATION");
			if (UtilValidate.isNotEmpty(postalAddress)) {
				String contactMechId = (String) postalAddress.get("contactMechId");
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
			
			// update notecontactId
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
			
			if (UtilValidate.isNotEmpty(entry.getString("assocPartyId"))) {
				conditions = new ArrayList<EntityCondition>();
				conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, entry.getString("assocPartyId")),
						EntityCondition.makeCondition("externalId", EntityOperator.EQUALS, entry.getString("assocPartyId"))
						));
		    	mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
		    	GenericValue assocParty = EntityQuery.use(delegator).from("Party").where(mainConditon).queryFirst();
    			if (UtilValidate.isNotEmpty(assocParty) && UtilValidate.isNotEmpty(assocParty.getString("roleTypeId"))) {
    				callCtxt = new HashMap<String, Object>();
    				callCtxt.put("partyId", partyId);
    				callCtxt.put("assocPartyId", assocParty.getString("partyId"));
    				callCtxt.put("roleTypeId", assocParty.getString("roleTypeId"));
    				callCtxt.put("userLogin", userLogin);
    				callResult = dispatcher.runSync("common.performContactAndPartyAssoc", callCtxt);
    			}
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
		
		partyId = delegator.getNextSeqId("Party");
		
		String contactPartyId = entry.getString("contactId");
		if (UtilValidate.isEmpty(contactPartyId)) {
			contactPartyId = partyId;
		}
		
		List<GenericValue> partyValues = FastList.newInstance();
		
		partyValues.add(delegator.makeValue("Party", UtilMisc.toMap("partyId", partyId, "roleTypeId", "CONTACT", "partyTypeId", "PERSON", "externalId", contactPartyId, "createdDate",UtilDateTime.nowTimestamp(), "statusId", "PARTY_ENABLED"
				, "description", entry.getString("description"), "timeZoneDesc", entry.getString("timeZoneId"), "createdByUserLogin", userLogin.getString("userLoginId"))));
		partyValues.add(delegator.makeValue("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "CONTACT")));
		toBeStored.addAll(partyValues);
		
		GenericValue person = delegator.makeValue("Person",
				UtilMisc.toMap("partyId", partyId, "firstName", entry.getString("firstName"), "lastName", entry.getString("lastName"), "middleName", entry.getString("middleName")));
		person.put("gender", entry.getString("gender"));
		person.put("personalTitle", entry.getString("personalTitle"));
		person.put("occupation", entry.getString("occupation"));
		person.put("maritalStatus", entry.getString("maritalStatus"));
		person.put("designation", entry.getString("designation"));
		if (UtilValidate.isNotEmpty(entry.getString("birthDate"))) {
			person.put("birthDate", org.fio.homeapps.util.UtilDateTime.toSqlDate(entry.getString("birthDate")));
		}
		
		toBeStored.add(person);

		if (UtilValidate.isNotEmpty(entry.getString("primaryPhoneNumber"))
				&& UtilValidate.isNotEmpty(entry.getString("secondaryPhoneNumber"))) {
			Map<String, Object> dndStatusPrimaryPhoneMp = DataUtil.getDndStatus(delegator,
					entry.getString("primaryPhoneNumber"));
			Map<String, Object> dndStatusSecondaryPhoneMp = DataUtil.getDndStatus(delegator,
					entry.getString("secondaryPhoneNumber"));
		}
		
		Debug.logInfo("Creating Person [" + partyId + "] for Contact [" + entry.get("contactId") + "].", MODULE);

		GenericValue partySupplementalData = delegator.makeValue("PartySupplementalData", UtilMisc.toMap("partyId", partyId));

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
		
		if (UtilValidate.isNotEmpty(entry.getString("assocPartyId"))) {
			conditions = new ArrayList<EntityCondition>();
			conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
					EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, entry.getString("assocPartyId")),
					EntityCondition.makeCondition("externalId", EntityOperator.EQUALS, entry.getString("assocPartyId"))
					));
	    	mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
	    	GenericValue assocParty = EntityQuery.use(delegator).from("Party").where(mainConditon).queryFirst();
			if (UtilValidate.isNotEmpty(assocParty) && UtilValidate.isNotEmpty(assocParty.getString("roleTypeId"))) {
				callCtxt = new HashMap<String, Object>();
				callCtxt.put("partyId", partyId);
				callCtxt.put("assocPartyId", assocParty.getString("partyId"));
				callCtxt.put("roleTypeId", assocParty.getString("roleTypeId"));
				callCtxt.put("userLogin", userLogin);
				callResult = dispatcher.runSync("common.performContactAndPartyAssoc", callCtxt);
			}
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
		
		//partySupplementalData.put("industryCat", entry.get("industryCat"));
		//partySupplementalData.put("industry", entry.get("industry"));
		//partySupplementalData.put("industryEnumId", entry.get("industry"));
		//partySupplementalData.put("customerTradingType", entry.get("customerTradingType"));
		//partySupplementalData.put("keyContactPerson1", entry.get("keyContactPerson1"));
		//partySupplementalData.put("keyContactPerson2", entry.get("keyContactPerson2"));
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

		//partySupplementalData.put("source", entry.get("source"));
		//partySupplementalData.put("virtualTeamId", entry.get("virtualTeamId"));
	}
    
    public static String getFirstValidRoleTypeId(String partyId, List<String> possibleRoleTypeIds, Delegator delegator) throws GenericEntityException {

        List<GenericValue> partyRoles = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", partyId),null,false);

        // iterate across all possible roleTypeIds from the parameter
        for (String possibleRoleTypeId : possibleRoleTypeIds) {
            // try to look for each one in the list of PartyRoles
            for (GenericValue partyRole : partyRoles) {
                if (possibleRoleTypeId.equals(partyRole.getString("roleTypeId")))  {
                    return possibleRoleTypeId;
                }
            }
        }
        return null;
    }
}
