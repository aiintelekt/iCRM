import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.util.EntityQuery;
import java.util.HashMap;
import net.sf.json.JSONObject;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.groupfio.account.portal.contactmech.PartyPrimaryContactMechWorker;
import org.fio.crm.constants.CrmConstants
import org.fio.crm.party.PartyHelper;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.base.util.Debug;
import org.ofbiz.service.ServiceUtil;
import java.util.*;
import java.sql.ResultSet;
import java.sql.Timestamp;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastList;
import java.text.DecimalFormat;
import org.fio.homeapps.util.UtilActivity;
import org.fio.homeapps.util.EnumUtil;


delegator = request.getAttribute("delegator");
String partyId = ""; //request.getParameter("partyId");
String srNumber = request.getParameter("srNumber");
GenericValue custRequest = EntityQuery.use(delegator).from("CustRequest").where("custRequestId",srNumber).queryFirst();
if(UtilValidate.isNotEmpty(custRequest)) {
	partyId = custRequest.getString("fromPartyId");
}

if(UtilValidate.isNotEmpty(partyId)) {
	String isView = context.get("isView");
	
	inputContext = new LinkedHashMap<String, Object>();
	
	inputContext.put("partyId", partyId);
	
	dispatcher = request.getAttribute("dispatcher");
	
	validRoleTypeId = PartyHelper.getFirstValidRoleTypeId(partyId, UtilMisc.toList("LEAD","ACCOUNT"), delegator);
	accountActive = PartyHelper.isActive(partyId, delegator);
	partySummary = from("PartySummaryDetailsView").where("partyId", partyId,"partyTypeId","PARTY_GROUP").queryOne();
	
	if (UtilValidate.isNotEmpty(validRoleTypeId) && validRoleTypeId.equals("LEAD")) {
	
		if(partySummary!=null && partySummary.size()>0){
			inputContext.putAll(partySummary.getAllFields());
			inputContext.put("leadName", partySummary.get("groupName"));
			inputContext.put("currencyUomId", partySummary.get("preferredCurrencyUomId"));
			//inputContext.put("email", partySummary.get("primaryEmail"));
			//inputContext.put("contactNumber", partySummary.get("primaryContactNumber"));
		}
	
		// gather data that should only be available for active lead
		if (accountActive) {
			// set this flag to allow contact mechs to be shown
			request.setAttribute("displayContactMechs", "Y");
			context.put("displayContactMechs", "Y");
	
			// Provide current PartyClassificationGroups as a list and a string
			groups = PartyHelper.getClassificationGroupsForParty(partyId, delegator);
			context.put("partyClassificationGroups", groups);
			descriptions = EntityUtil.getFieldListFromEntityList(groups, "customFieldName", false);
			inputContext.put("segment", StringUtil.join(descriptions, ", "));
			if(UtilValidate.isNotEmpty(groups)){
				customIds = EntityUtil.getFieldListFromEntityList(groups, "customFieldId", false);
				inputContext.put("partyClassificationGroupId", customIds.get(0));
			}
	
			curStatusItem = from("StatusItem").where("statusId", partySummary.get("statusId")).queryOne();
			if(UtilValidate.isNotEmpty(curStatusItem)){
				inputContext.put("leadStatus", curStatusItem.description);
			}
		}else {
			inputContext.put("leadStatus", "Disable");
		}
	
		Map<String, Object> primaryContact = org.groupfio.common.portal.util.DataUtil.getPrimaryContact(delegator, partyId, "LEAD");
		if (UtilValidate.isNotEmpty(primaryContact)) {
			primaryContactId = (String) primaryContact.get("contactId");
			primaryContactName = (String) primaryContact.get("contactName");
			primaryContactEmail = org.fio.homeapps.util.PartyHelper.getEmailAddress(delegator, primaryContactId, "PRIMARY_EMAIL");
			primaryContactPhone = org.fio.homeapps.util.PartyHelper.getContactNumber(delegator, primaryContactId, "PRIMARY_PHONE");
	
			//added for getting current time of primary contact
			primaryContactTZ = from("PartySummaryDetailsView").where("partyId", primaryContactId).queryOne();
			if(UtilValidate.isNotEmpty(primaryContactTZ.getString("timeZoneDesc"))){
				currentTimeForTimezone = UtilActivity.getCurrentTimeFromTimeZone(delegator,primaryContactTZ.getString("timeZoneDesc"));
				inputContext.put("currentTimeForTimezone", currentTimeForTimezone);
			}
			//ended
	
			inputContext.put("PrimaryContact", primaryContactName);
			inputContext.put("PrimaryContactEmail", primaryContactEmail);
			inputContext.put("PrimaryContactPhone", primaryContactPhone);
		}
	
		String relationshipManager = org.fio.homeapps.util.PartyHelper.getCurrentResponsiblePartyName(partyId, "LEAD", delegator);
		inputContext.put("personResponsible", relationshipManager);
	
		context.put("inputContext", inputContext);
		context.put("domainEntityId", partyId);
		context.put("domainEntityType", "LEAD");
		context.put("requestURI", "viewLead");
		context.put("partyId", partyId);
	
	}
	
	
	if (UtilValidate.isNotEmpty(validRoleTypeId) && validRoleTypeId.equals("ACCOUNT")) {
	
		if(partySummary!=null && partySummary.size()>0){
			inputContext.putAll(partySummary.getAllFields());
			inputContext.put("accountName", partySummary.get("groupName"));
			inputContext.put("currencyUomId", partySummary.get("preferredCurrencyUomId"));
			//inputContext.put("email", partySummary.get("primaryEmail"));
			//inputContext.put("contactNumber", partySummary.get("primaryContactNumber"));
		}
	
		if (accountActive) {
			// set this flag to allow contact mechs to be shown
			request.setAttribute("displayContactMechs", "Y");
			context.put("displayContactMechs", "Y");
			// who is currently responsible for account
			responsibleParty = PartyHelper.getCurrentResponsibleParty(partyId, "ACCOUNT", delegator);
			context.put("responsibleParty", responsibleParty);
	
			// Provide current PartyClassificationGroups as a list and a string
			groups = PartyHelper.getClassificationGroupsForParty(partyId, delegator);
			context.put("partyClassificationGroups", groups);
			descriptions = EntityUtil.getFieldListFromEntityList(groups, "customFieldName", false);
			inputContext.put("segment", StringUtil.join(descriptions, ", "));
			if(UtilValidate.isNotEmpty(groups)){
				customIds = EntityUtil.getFieldListFromEntityList(groups, "customFieldId", false);
				inputContext.put("partyClassificationGroupId", customIds.get(0));
			}
	
			party =  from("Party").where("partyId", partyId).queryOne();
			if (UtilValidate.isNotEmpty(party)) {
				partyStatus=party.statusId;
			}
			if (UtilValidate.isNotEmpty(partyStatus)) {
				curStatus= from("StatusItem").where("statusId", partyStatus).queryOne();
				if (UtilValidate.isNotEmpty(curStatus)){
					statusDecription=curStatus.description;
				}
				inputContext.put("accountStatus", statusDecription);
			}
		} else {
			accountDeactivationDate = PartyHelper.getDeactivationDate(partyId, delegator);
			context.put("accountDeactivated", true);
			context.put("accountDeactivatedDate", accountDeactivationDate);
			context.put("validView", true);  // can still view history of deactivated contacts
	
			party =  from("Party").where("partyId", partyId).queryOne();
			if (UtilValidate.isNotEmpty(party)) {
				partyStatus=party.statusId;
			}
			if (UtilValidate.isNotEmpty(partyStatus)) {
				curStatus= from("StatusItem").where("statusId", partyStatus).queryOne();
				if (UtilValidate.isNotEmpty(curStatus)){
					statusDecription=curStatus.description;
				}
				inputContext.put("accountStatus", statusDecription);
			}
		}
	
		Map<String, Object> contactAcctMap = new HashMap<String, Object>();
		contactAcctMap.put("partyIdTo", partyId);
		contactAcctMap.put("partyRoleTypeId", "ACCOUNT");
		Map<String, Object> result = dispatcher.runSync("common.getContactAndPartyAssoc", contactAcctMap);
		if(ServiceUtil.isSuccess(result)){
			context.partyContactAssocList = result.partyContactAssoc;
			if (UtilValidate.isNotEmpty(result)){
				List primaryContactsList = new ArrayList();
				primaryContactsList = result.partyContactAssoc;
				String primaryContactName = "";
				String contactId = "";String primaryCId = "";
				for(int i=0;i<primaryContactsList.size();i++){
					Map < String, Object > partyContactMap = new HashMap < String, Object > ();
					partyContactMap = (Map<String, Object>) primaryContactsList.get(i);
					if(i==0){
						primaryContactName = (String) partyContactMap.get("name");
						contactId = (String) partyContactMap.get("contactId");
					}
					String primaryContactStatusId =  partyContactMap.get("statusId");
					if("PARTY_DEFAULT".equals(primaryContactStatusId)){
						primaryContactName = (String) partyContactMap.get("name");
						contactId = (String) partyContactMap.get("contactId");
						primaryCId = (String) partyContactMap.get("contactId");
					}
				}
				inputContext.put("PrimaryContact", primaryContactName);
				if(contactId!=null){
					primaryContactInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,contactId,UtilMisc.toMap("isRetrivePhone", true, "isRetriveEmail", true),true);
					if(UtilValidate.isNotEmpty(primaryContactInformation)) {
						inputContext.put("PrimaryContactEmail",primaryContactInformation.get("EmailAddress"));
						inputContext.put("PrimaryContactPhone",primaryContactInformation.get("PrimaryPhone"));
	
						//added for getting current time of primary contact
						if(UtilValidate.isNotEmpty(contactId)){
							primaryContactTZ = from("PartySummaryDetailsView").where("partyId", contactId).queryOne();
							if(UtilValidate.isNotEmpty(primaryContactTZ.getString("timeZoneDesc"))){
								currentTimeForTimezone = UtilActivity.getCurrentTimeFromTimeZone(delegator,primaryContactTZ.getString("timeZoneDesc"));
								inputContext.put("currentTimeForTimezone", currentTimeForTimezone);
							}
						}
						//ended
	
					}
				}
			}
		}
	
		PrimaryPhone = EntityUtil.getFirst(delegator.findByAnd("PartyContactMechPurpose",UtilMisc.toMap("contactMechPurposeTypeId":"PRIMARY_PHONE", "partyId":partyId ),null,false));
		if(UtilValidate.isNotEmpty(PrimaryPhone)){
			contactMechId = PrimaryPhone.get("contactMechId");
	
			PrimaryPhone = EntityUtil.getFirst(delegator.findByAnd("TelecomNumber",UtilMisc.toMap("contactMechId":contactMechId),null,false));
			PrimaryContact = EntityUtil.getFirst(delegator.findByAnd("PartyContactMech",UtilMisc.toMap("contactMechId":contactMechId),null,false));
			allowSolicitation = PrimaryContact.get("allowSolicitation");
			if(UtilValidate.isNotEmpty(allowSolicitation) && allowSolicitation == "Y"){
				//inputContext.put("PrimaryContact","Yes");
			}else if(UtilValidate.isNotEmpty(allowSolicitation) && allowSolicitation == "N"){
				//inputContext.put("PrimaryContact","No");
			}
	
			contactNumber = PrimaryPhone.get("contactNumber");
	
			//inputContext.put("PrimaryContactPhone",contactNumber);
		}
	
		PrimaryEmail = EntityUtil.getFirst(delegator.findByAnd("PartyContactMechPurpose",UtilMisc.toMap("contactMechPurposeTypeId":"PRIMARY_EMAIL", "partyId":partyId ),null,false));
		if(UtilValidate.isNotEmpty(PrimaryEmail)){
			contactMechId = PrimaryEmail.get("contactMechId");
	
			PrimaryEmail = EntityUtil.getFirst(delegator.findByAnd("ContactMech",UtilMisc.toMap("contactMechId":contactMechId),null,false));
	
			email = PrimaryEmail.get("infoString");
	
			//inputContext.put("PrimaryContactEmail",email);
		}
		PrimaryWeb = EntityUtil.getFirst(delegator.findByAnd("PartyContactMechPurpose",UtilMisc.toMap("contactMechPurposeTypeId":"PRIMARY_WEB_URL", "partyId":partyId ),null,false));
		if(UtilValidate.isNotEmpty(PrimaryWeb)){
			contactMechId = PrimaryWeb.get("contactMechId");
	
			PrimaryWeb = EntityUtil.getFirst(delegator.findByAnd("ContactMech",UtilMisc.toMap("contactMechId":contactMechId),null,false));
	
			webAddr = PrimaryWeb.get("infoString");
	
			inputContext.put("primaryWebUrl",webAddr);
		}
		PartyRelationship = EntityUtil.getFirst(delegator.findByAnd("PartyRelationship",UtilMisc.toMap("partyRelationshipTypeId":"RESPONSIBLE_FOR", "partyIdFrom":partyId ),UtilMisc.toList("fromDate DESC"),false));
		if(UtilValidate.isNotEmpty(PartyRelationship)){
			partyIdTo = PartyRelationship.get("partyIdTo");
	
			Person = EntityUtil.getFirst(delegator.findByAnd("Person",UtilMisc.toMap("partyId":partyIdTo),null,false));
			if(UtilValidate.isNotEmpty(Person)){
				personResponsible1 = Person.get("firstName")+" "+Person.get("lastName");
				inputContext.put("personResponsible",personResponsible1);
	
			}
		}
	
		context.put("inputContext", inputContext);
		relationManager =org.fio.homeapps.util.PartyHelper.getCurrentResponsibleParty(partyId, "ACCOUNT", delegator);
		String rMId=UtilValidate.isNotEmpty(relationManager)?relationManager.partyId:null;
		context.put("selectedRMId", rMId);
		context.put("domainEntityId", partyId);
		context.put("domainEntityType", "ACCOUNT");
		context.put("requestURI", "viewAccount");
		context.put("partyId", partyId);
	
	}
}
