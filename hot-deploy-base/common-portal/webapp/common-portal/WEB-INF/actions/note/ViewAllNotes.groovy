import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.util.EntityFindOptions
import org.ofbiz.entity.util.EntityQuery;
import java.util.HashMap;
import java.util.TimeZone;
import org.fio.crm.party.PartyHelper;
import org.fio.homeapps.util.EnumUtil;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity
import org.ofbiz.entity.model.ModelKeyMap
import org.ofbiz.base.util.Debug;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastList;

import org.groupfio.common.portal.CommonPortalConstants.DomainEntityType;
import org.groupfio.common.portal.CommonPortalConstants;

delegator = request.getAttribute("delegator");
String userLoginId = userLogin.getString("userLoginId");
String noteIdsList = request.getParameter("noteIdsList");

String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);

String domainEntityType = request.getParameter("domainEntityType");
String domainEntityId = request.getParameter("domainEntityId");
String campaignListId = request.getParameter("campaignListId");
String requestURI = request.getParameter("requestURI");

context.put("domainEntityType", domainEntityType);
context.put("domainEntityId", domainEntityId);

String noteTabId = 'a-notes';
if (UtilValidate.isNotEmpty(domainEntityType)) {
    if (domainEntityType.equals("SERVICE_REQUEST")) {
        noteTabId = 'sr-notes';
    } else if (domainEntityType.equals("CUSTOMER")) {
        noteTabId = 'c-notes';
    } else if (domainEntityType.equals("CONTACT")) {
        noteTabId = 'contact-notes';
    } else if (domainEntityType.equals("OPPORTUNITY")) {
        noteTabId = 'opportunity-notes';
    }
}

String domainEntityTypeDesc = "FSR";
if (UtilValidate.isNotEmpty(domainEntityType) && !domainEntityType.equals("SERVICE_REQUEST")) {
    domainEntityTypeDesc = org.groupfio.common.portal.util.DataHelper.convertToLabel(domainEntityType);
}
context.put("domainEntityTypeDesc", domainEntityTypeDesc);

if (requestURI.contains("client-portal")) {
    context.put("domainEntityLink", "/client-portal/control/viewServiceRequest?srNumber=" + domainEntityId + "&externalLoginKey=" + externalLoginKey + "#sr-notes")
} else if(UtilValidate.isNotEmpty(campaignListId))
	context.put("domainEntityLink", "/customer-portal/control/viewCallListCustomer?partyId=" + domainEntityId + "&campaignListId=" + campaignListId + "&externalLoginKey=" + externalLoginKey + "#c-notes")
else
    context.put("domainEntityLink", org.groupfio.common.portal.util.DataHelper.prepareLinkedFrom(domainEntityId, domainEntityType, externalLoginKey) + "#" + noteTabId);

context.put("domainEntityName", org.groupfio.common.portal.util.DataHelper.getDomainEntityName(delegator, domainEntityId, domainEntityType));

String workEffortId = request.getParameter("workEffortId");

List < Map < String, Object >> dataList = new LinkedList < Map < String, Object >> ();

List conditionList = FastList.newInstance();

String partyIdStatus = "";
String partyId = null;
if (UtilValidate.isNotEmpty(domainEntityType) && CommonPortalConstants.PARTY_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
    partyId = domainEntityId;
}

if (UtilValidate.isNotEmpty(partyId)) {
    GenericValue partySummary = delegator.findOne("PartySummaryDetailsView",
        UtilMisc.toMap("partyId", partyId), false);
    partyIdStatus = (String) partySummary.get("statusId");
}
if (UtilValidate.isNotEmpty(partyId) && (UtilValidate.isNotEmpty(domainEntityType) && CommonPortalConstants.PARTY_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType))) {
    if (UtilValidate.isNotEmpty(partyId)) {
        conditionList.add(EntityCondition.makeCondition("targetPartyId", EntityOperator.EQUALS, partyId));
    }
}

// TODO this code block no actual use
/*
boolean isAccount = false;
if (UtilValidate.isNotEmpty(partyId) && (UtilValidate.isNotEmpty(domainEntityType) && CommonPortalConstants.PARTY_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType))) {
    GenericValue partyRole = delegator.findOne("PartyRole",
        UtilMisc.toMap("partyId", partyId, "roleTypeId", "ACCOUNT"), false);
    if (UtilValidate.isNotEmpty(partyRole)) {
        isAccount = true;
    }
    if (isAccount) {
        EntityFindOptions efo1 = new EntityFindOptions();

        List partyFromRelnListNote = delegator.findList(
            "PartyFromByRelnAndContactInfoAndPartyClassification",
            EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
        if (partyFromRelnListNote != null && partyFromRelnListNote.size() > 0) {
            List partyFromRelnNote = EntityUtil.getFieldListFromEntityList(partyFromRelnListNote, "partyIdFrom", true);
            if (partyFromRelnNote != null && partyFromRelnNote.size() > 0) {
                conditionList.add(EntityCondition.makeCondition("targetPartyId", EntityOperator.EQUALS, partyId));
            }
        }
    }
}
*/

String entityName = "PartyNoteView";
if (UtilValidate.isNotEmpty(domainEntityType) && domainEntityType.equals(DomainEntityType.OPPORTUNITY)) {
    entityName = "OpportunityNoteView";
    conditionList.add(
        EntityCondition.makeCondition("salesOpportunityId", EntityOperator.EQUALS, domainEntityId));
} else if (UtilValidate.isNotEmpty(domainEntityType) && (domainEntityType.equals(DomainEntityType.SUBSCRIPTION) || domainEntityType.equals(DomainEntityType.SUBS_PRODUCT) || domainEntityType.equals(DomainEntityType.REBATE) || domainEntityType.equals(DomainEntityType.APV_TPL))) {
    entityName = "CommonNoteView";
    conditionList
        .add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, domainEntityId));
    conditionList.add(
        EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, domainEntityType));
} else if (UtilValidate.isNotEmpty(domainEntityType) && domainEntityType.equals(DomainEntityType.SERVICE_REQUEST)) {
    entityName = "CustRequestNoteView";

    List < String > noteIds = new ArrayList < String > ();
    if (UtilValidate.isNotEmpty(workEffortId)) {
        List < GenericValue > workEffortNotes = EntityQuery.use(delegator).from("WorkEffortNote")
            .where("workEffortId", workEffortId).queryList();
        noteIds = UtilValidate.isNotEmpty(workEffortNotes) ?
            EntityUtil.getFieldListFromEntityList(workEffortNotes, "noteId", true) :
            new ArrayList < > ();
        if (UtilValidate.isNotEmpty(noteIds)) {
            conditionList.add(EntityCondition.makeCondition("noteId", EntityOperator.IN, noteIds));
        }
        domainEntityType = DomainEntityType.ACTIVITY;
        domainEntityId = workEffortId;
    } else {
        conditionList
            .add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, domainEntityId));
    }
}

if (UtilValidate.isNotEmpty(noteIdsList)) {
    println('noteIdsList: ' + noteIdsList);
    conditionList.add(EntityCondition.makeCondition("noteId", EntityOperator.IN, Arrays.asList(noteIdsList.split(","))));
}

if (UtilValidate.isNotEmpty(conditionList)) {
    EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    println('note mainConditons: ' + mainConditons);
    List < GenericValue > noteList = delegator.findList(entityName, mainConditons, null, UtilMisc.toList("noteDateTime DESC"), null, false);

    if (UtilValidate.isNotEmpty(noteList)) {
        for (GenericValue entry: noteList) {
            Map < String, Object > data = new HashMap < String, Object > ();
            data = org.fio.admin.portal.util.DataUtil.convertGenericValueToMap(delegator, entry);
            String noteId = entry.getString("noteId");
            data.put("domainEntityId", domainEntityId);
            data.put("domainEntityType", domainEntityType);
            data.put("domainEntityTypeDesc", org.groupfio.common.portal.util.DataHelper.convertToLabel(domainEntityType));

            data.put("noteDateTime",
                UtilValidate.isNotEmpty(entry.get("noteDateTime")) ?
                UtilDateTime.timeStampToString(entry.getTimestamp("noteDateTime"),
                    globalDateTimeFormat, TimeZone.getDefault(), null) :
                "");

            data.put("noteType", EnumUtil.getEnumDescription(delegator, entry.getString("noteType")));
            data.put("createdByName",
                PartyHelper.getPartyName(delegator, org.fio.homeapps.util.DataUtil
                    .getPartyIdByUserLoginId(delegator, entry.getString("createdByUserLogin")),
                    false));
            data.put("createdByRole", org.fio.homeapps.util.DataUtil.getRoleTypeDesc(delegator,
                entry.getString("createdByUserLoginRoleTypeId")));

            if (UtilValidate.isNotEmpty(entry.getString("noteParty"))) {
                data.put("notePartyName", PartyHelper.getPartyName(delegator, entry.getString("noteParty"), false));
            }
            if (UtilValidate.isNotEmpty(partyIdStatus)) {
                data.put("partyIdStatus", partyIdStatus);
            }
            data.put("loginUser", userLoginId);
            dataList.add(data);
        }
    }
}

context.noteList = dataList;
