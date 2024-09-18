import org.groupfio.common.portal.CommonPortalConstants
import org.groupfio.common.portal.CommonPortalConstants.DomainEntityType
import org.groupfio.common.portal.event.AjaxEvents
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil

import javolution.util.FastList

String partyId = request.getParameter("partyId");

String requestUri = request.getRequestURI();
println ("requestUri------->"+requestUri);

if(UtilValidate.isNotEmpty(userLogin)) {
	loggedUserPartyId = userLogin.getString("partyId");
	loggedUserName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, loggedUserPartyId, false);
	context.put("loggedUserPartyName", loggedUserName);
	context.put("loggedUserId", userLogin.getString("userLoginId"));
}
String userLoginId = userLogin.getString("userLoginId");
String domainType = UtilValidate.isNotEmpty(context.get("domainEntityType")) ? context.get("domainEntityType") : request.getParameter("domainEntityType");
String domainId = UtilValidate.isNotEmpty(context.get("domainEntityId")) ? context.get("domainEntityId") : request.getParameter("domainEntityId");


println ("domainType===============>"+domainType);
//get important note count
List conditionList = FastList.newInstance();
List<Map<String, Object>> dataList = new LinkedList<Map<String, Object>>();


String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);

if (UtilValidate.isNotEmpty(partyId) && (UtilValidate.isNotEmpty(domainType) && CommonPortalConstants.PARTY_DOMAIN_ENTITY_TYPE.containsKey(domainType))) {
	if (UtilValidate.isNotEmpty(partyId)) {
		conditionList.add(EntityCondition.makeCondition("targetPartyId", EntityOperator.EQUALS, partyId));
	}
}

String entityName = "PartyNoteView";
if (UtilValidate.isNotEmpty(domainType) && domainType.equals(DomainEntityType.OPPORTUNITY)) {
	String salesOpportunityId = request.getParameter("salesOpportunityId");
	entityName = "OpportunityNoteView";
	conditionList.add(EntityCondition.makeCondition("salesOpportunityId", EntityOperator.EQUALS, salesOpportunityId));
} else if (UtilValidate.isNotEmpty(domainType) && (domainType.equals(DomainEntityType.SUBSCRIPTION)	|| domainType.equals(DomainEntityType.SUBS_PRODUCT) || domainType.equals(DomainEntityType.REBATE) || domainType.equals(DomainEntityType.APV_TPL))) {
	String domainEntityId = request.getParameter("domainEntityId");
	entityName = "CommonNoteView";
	conditionList
			.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, domainEntityId));
	conditionList.add(
			EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, domainType));
} else if (UtilValidate.isNotEmpty(domainType) && domainType.equals(DomainEntityType.SERVICE_REQUEST)) {
	entityName = "CustRequestNoteView";
	String workEffortId = request.getParameter("workEffortId");
	List<String> noteIds = new ArrayList<String>();
	if (UtilValidate.isNotEmpty(workEffortId)) {
		List<GenericValue> workEffortNotes = EntityQuery.use(delegator).from("WorkEffortNote")
				.where("workEffortId", workEffortId).queryList();
		noteIds = UtilValidate.isNotEmpty(workEffortNotes)
				? EntityUtil.getFieldListFromEntityList(workEffortNotes, "noteId", true)
				: new ArrayList<>();
		if (UtilValidate.isNotEmpty(noteIds)) {
			conditionList.add(EntityCondition.makeCondition("noteId", EntityOperator.IN, noteIds));
		}
		domainEntityType = DomainEntityType.ACTIVITY;
		domainEntityId = workEffortId;
	} else {
		String custRequestId = request.getParameter("srNumber");
		if(UtilValidate.isEmpty(custRequestId)) {
			custRequestId = request.getAttribute("srNumber");
			if(UtilValidate.isNotEmpty(request.getParameter("custRequestId"))){
				custRequestId = request.getParameter("custRequestId");
			}
		}
		if(UtilValidate.isEmpty(custRequestId)) {
			custRequestId = request.getParameter("domainEntityId");
		}

		conditionList
				.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
	}
}

conditionList.add(EntityCondition.makeCondition("isImportant", EntityOperator.EQUALS,"Y"));
long importantNoteCount = 0l;
if (UtilValidate.isNotEmpty(conditionList)) {
	EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	println('isImportant note mainConditons: '+mainConditons);
	importantNoteCount = EntityQuery.use(delegator).from(entityName).where(mainConditons).queryCount();
}

Map<String, Object> data = new HashMap<String, Object>();

data.put("impNoteCount", importantNoteCount);

return AjaxEvents.doJSONResponse(response, data);
