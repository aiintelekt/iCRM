import org.fio.homeapps.constants.GlobalConstants.AccessLevel
import org.groupfio.common.portal.CommonPortalConstants;
import org.groupfio.common.portal.CommonPortalConstants.DomainEntityType;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity
import org.ofbiz.entity.model.ModelKeyMap
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher

import javolution.util.FastList;

Delegator delegator = request.getAttribute("delegator");
LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
GenericValue userLogin = request.getAttribute("userLogin");

uiLabelMap = UtilProperties.getResourceBundleMap("common-portalUiLabels", locale);

String partyId = request.getParameter("partyId");

String requestUri = request.getRequestURI();
println ("requestUri------->"+requestUri);

String accessLevel = "Y";
String opLevel = "L1";
String businessUnit = null;
List<Map<String, Object>> buInfo = new ArrayList<Map<String, Object>>();
Map<String, Object> accessMatrixRes = new HashMap<String, Object>();


if(UtilValidate.isNotEmpty(userLogin)) {
	loggedUserPartyId = userLogin.getString("partyId");
	loggedUserName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, loggedUserPartyId, false);
	context.put("loggedUserPartyName", loggedUserName);
	context.put("loggedUserId", userLogin.getString("userLoginId"));
}
String userLoginId = userLogin.getString("userLoginId");
String domainType = context.get("domainEntityType");
String domainId = context.get("domainEntityId");


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
int importantNoteCount = 0;
if (UtilValidate.isNotEmpty(conditionList)) {
	EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	println('isImportant note mainConditons: '+mainConditons);
	List<GenericValue> noteList = delegator.findList(entityName, mainConditons, null, UtilMisc.toList("noteDateTime DESC"), null, false);
	
	if (UtilValidate.isNotEmpty(noteList)) {
		importantNoteCount = noteList.size();
	}

}
context.put("ab-im-notes", importantNoteCount);

long openSrCount = 0l;
if(UtilValidate.isNotEmpty(domainType) && CommonPortalConstants.PARTY_DOMAIN_ENTITY_TYPE.containsKey(domainType)) {
	
	if (UtilValidate.isNotEmpty(userLoginId)) {
		String userLoginPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, userLoginId);
		businessUnit = org.fio.homeapps.util.DataUtil.getBusinessUnitId(delegator, userLoginPartyId);
		Map<String, Object> accessMatrixMap = new LinkedHashMap<String, Object>();
		accessMatrixMap.put("delegator", delegator);
		accessMatrixMap.put("dispatcher", dispatcher);
		accessMatrixMap.put("businessUnit", businessUnit);
		accessMatrixMap.put("modeOfOp", "Read");
		accessMatrixMap.put("entityName", "CustRequest");
		accessMatrixMap.put("userLoginId", userLoginId);
		accessMatrixRes = org.fio.homeapps.util.DataUtil.getAccessList(accessMatrixMap);
		if (UtilValidate.isNotEmpty(accessMatrixRes)) {
			accessLevel = (String) accessMatrixRes.get("accessLevel");
		} else {
			accessLevel = null;
		}
	}
	
	
	
	if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
		
		conditionList = new ArrayList<EntityCondition>();
		// check with ownerId
		if (UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("ownerId")) {
			@SuppressWarnings("unchecked")
			List<String> ownerIds = (List<String>) accessMatrixRes.get("ownerId");
			conditionList.add(EntityCondition.makeCondition("owner", EntityOperator.IN, ownerIds));
		}

		// check with emplTeamId
		if (UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("emplTeamId")) {
			@SuppressWarnings("unchecked")
			List<String> emplTeamIds = (List<String>) accessMatrixRes.get("emplTeamId");
			conditionList.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.IN, emplTeamIds));
		}
		
		if (UtilValidate.isNotEmpty(partyId)) {
			String partySecurityRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, partyId);
			conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("assocPartyId", EntityOperator.EQUALS, partyId),
					EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, partySecurityRole)
					));
		}
		
		List<EntityCondition> flagConditions = new ArrayList<EntityCondition>();
		List<String> statuses = new ArrayList<String>();
		statuses.add(CommonPortalConstants.srOpenStatuses.SR_ASSIGNED);
		statuses.add(CommonPortalConstants.srOpenStatuses.SR_OPEN);
		statuses.add(CommonPortalConstants.srOpenStatuses.SR_IN_PROGRESS);
		statuses.add(CommonPortalConstants.srOpenStatuses.SR_PENDING);
		conditionList.add(EntityCondition.makeCondition("status", EntityOperator.IN, statuses));
		
		flagConditions.add(EntityCondition.makeCondition("status", EntityOperator.IN, statuses));
		
		EntityCondition condition = null;
		if (UtilValidate.isNotEmpty(domainType) && domainType.equals("CONTACT")) {
			List conditionsList = FastList.newInstance();

			conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			conditionsList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
			conditionsList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
			List<GenericValue> custRequestListList = delegator.findList("CustRequestContact", mainConditons,
					UtilMisc.toSet("custRequestId"), null, null, false);
			if (UtilValidate.isNotEmpty(custRequestListList)) {
				List<String> custRequestIds = EntityUtil.getFieldListFromEntityList(custRequestListList, "custRequestId", true);
				conditionList.clear();
				conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.IN, custRequestIds));
				EntityCondition conditions = EntityCondition.makeCondition(flagConditions, EntityOperator.OR);
				conditionList.add(conditions);
				condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			}

		} else {
			condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		}
		if(UtilValidate.isNotEmpty(condition))
			openSrCount = EntityQuery.use(delegator).from("CustRequestSummary2").where(condition).queryCount();
		
	}
}
context.put("ab-sr", openSrCount);

//get oppo count
long openOppoCount = 0l;
if(UtilValidate.isNotEmpty(domainType) && CommonPortalConstants.PARTY_DOMAIN_ENTITY_TYPE.containsKey(domainType)) {
	
	conditionList = new ArrayList<EntityCondition>();
	
	if (UtilValidate.isNotEmpty(partyId)) {
		String partySecurityRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, partyId);
		
		conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
			EntityCondition.makeCondition("soPartyId", EntityOperator.EQUALS, partyId),
			EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, partySecurityRole)
			));
	}
	
	List<String> oppoStatusList = new ArrayList<String>();
	oppoStatusList.add("OPPO_OPEN");
	
	conditionList.add(EntityCondition.makeCondition("opportunityStatusId", EntityOperator.IN, oppoStatusList));
	
	EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	DynamicViewEntity dynamicView = new DynamicViewEntity();
	dynamicView.addMemberEntity("SO", "SalesOpportunity");
	dynamicView.addAlias("SO", "salesOpportunityId");
	dynamicView.addAlias("SO", "opportunityStatusId");
	dynamicView.addAlias("SO", "createdTxStamp");
	dynamicView.addAlias("SO", "lastUpdatedTxStamp");
	dynamicView.addAlias("SO", "soPartyId", "partyId", "", null, null, null);

	dynamicView.addMemberEntity("SOR", "SalesOpportunityRole");
	dynamicView.addAlias("SOR", "roleTypeId");
	dynamicView.addAlias("SOR", "sorPartyId", "partyId", "", false, false, null);
	dynamicView.addViewLink("SO", "SOR", Boolean.FALSE, ModelKeyMap.makeKeyMapList("salesOpportunityId"));
	
	openOppoCount = EntityQuery.use(delegator).from(dynamicView).where(condition).queryCount();
		
}
context.put("ab-oppo", openOppoCount);

//get open activity count
long openActivities = 0l;

if (UtilValidate.isNotEmpty(userLoginId)) {
	String userLoginPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, userLoginId);
	businessUnit = org.fio.homeapps.util.DataUtil.getBusinessUnitId(delegator, userLoginPartyId);
	Map<String, Object> accessMatrixMap = new LinkedHashMap<String, Object>();
	accessMatrixMap.put("delegator", delegator);
	accessMatrixMap.put("dispatcher", dispatcher);
	accessMatrixMap.put("businessUnit", businessUnit);
	accessMatrixMap.put("modeOfOp", "Read");
	accessMatrixMap.put("entityName", "WorkEffort");
	accessMatrixMap.put("userLoginId", userLoginId);
	accessMatrixRes = org.fio.homeapps.util.DataUtil.getAccessList(accessMatrixMap);
	if (UtilValidate.isNotEmpty(accessMatrixRes)) {
		accessLevel = (String) accessMatrixRes.get("accessLevel");
	} else {
		accessLevel = null;
	}
}

if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
	conditionList = FastList.newInstance();

	// check with ownerId
	if (UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("ownerId")) {
		@SuppressWarnings("unchecked")
		List<String> ownerIds = (List<String>) accessMatrixRes.get("ownerId");
		conditionList.add(EntityCondition.makeCondition("ownerId", EntityOperator.IN, ownerIds));
	}

	// check with emplTeamId
	if (UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("emplTeamId")) {
		@SuppressWarnings("unchecked")
		List<String> emplTeamIds = (List<String>) accessMatrixRes.get("emplTeamId");
		conditionList.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.IN, emplTeamIds));
	}

	if (UtilValidate.isNotEmpty(domainType) && domainType.equals(DomainEntityType.OPPORTUNITY)) {
		List conditionsList = FastList.newInstance();

		conditionsList.add(EntityCondition.makeCondition("salesOpportunityId", EntityOperator.EQUALS, domainId));

		EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);

		List<GenericValue> opportunityRoleList = delegator.findList("SalesOpportunityWorkEffort",
				mainConditons, UtilMisc.toSet("workEffortId"), null, null, false);
		if (UtilValidate.isNotEmpty(opportunityRoleList)) {
			List<String> workEffortIds = EntityUtil.getFieldListFromEntityList(opportunityRoleList,"workEffortId", true);

			conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIds));
		} else {
			conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, "999888999888"));
		}
		owner = null;
	}

	if (UtilValidate.isNotEmpty(domainType) && CommonPortalConstants.SERVICE_DOMAIN_ENTITY_TYPE.containsKey(domainType)) {

		owner = null;

		List conditionsList = FastList.newInstance();

		conditionsList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, domainId));

		EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);

		List<GenericValue> custRequestWorkEffortList = delegator.findList("CustRequestWorkEffort",mainConditons, UtilMisc.toSet("workEffortId"), null, null, false);
		if (UtilValidate.isNotEmpty(custRequestWorkEffortList)) {
			List<String> workEffortIds = EntityUtil.getFieldListFromEntityList(custRequestWorkEffortList,"workEffortId", true);

			conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIds));
		} else {
			conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, "999888999888"));
		}
	}

	String owner = UtilValidate.isNotEmpty(context.get("owner")) ? (String) context.get("owner") : partyId;

	if (UtilValidate.isNotEmpty(owner)) {
		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, owner));
	}
	List<String> actStatuses = new ArrayList<String>();
	actStatuses.add(CommonPortalConstants.activityOpenStatuses.IA_OPEN);
	actStatuses.add(CommonPortalConstants.activityOpenStatuses.IA_MIN_PROGRESS);
	actStatuses.add(CommonPortalConstants.activityOpenStatuses.IA_MSCHEDULED);
	if (UtilValidate.isNotEmpty(actStatuses)) {
		conditionList.add(EntityCondition.makeCondition("currentStatusId", EntityOperator.IN, actStatuses));
	}
	if (UtilValidate.isNotEmpty(domainType) && domainType.equals(DomainEntityType.CONTACT)) {
		List conditionsList = FastList.newInstance();

		conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, owner));
		conditionsList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
		conditionsList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
		EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);

		List<GenericValue> workEffortRoleList = delegator.findList("WorkEffortContact", mainConditons, UtilMisc.toSet("workEffortId"), null, null, false);
		if (UtilValidate.isNotEmpty(workEffortRoleList)) {
			List<String> workEffortIds = EntityUtil.getFieldListFromEntityList(workEffortRoleList, "workEffortId", true);
			conditionList.clear();
			conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIds));
			if (UtilValidate.isNotEmpty(actStatuses)) {
				conditionList.add(EntityCondition.makeCondition("currentStatusId", EntityOperator.IN, actStatuses));
			}
		}
	}
	String workEffortTypeIdNotIn = "WORK_FLOW";
	if (UtilValidate.isNotEmpty(workEffortTypeIdNotIn)) {
		List<String> workEffortTypeIdsNotIn = Arrays.asList(workEffortTypeIdNotIn.split(","));
		conditionList.add(EntityCondition.makeCondition("workEffortTypeId", EntityOperator.NOT_IN, workEffortTypeIdsNotIn));
	}
	
	DynamicViewEntity dynamicView = new DynamicViewEntity();

	dynamicView.addMemberEntity("WE", "WorkEffort");
	dynamicView.addAlias("WE", "workEffortId","workEffortId", null, Boolean.FALSE, Boolean.TRUE, null);
	dynamicView.addAlias("WE", "externalId");
	dynamicView.addAlias("WE", "workEffortTypeId");
	dynamicView.addAlias("WE", "currentStatusId");
	dynamicView.addAlias("WE", "lastUpdatedStamp");

	dynamicView.addMemberEntity("WEPA", "WorkEffortPartyAssignment");
	dynamicView.addAlias("WEPA", "partyId");
	dynamicView.addAlias("WEPA", "roleTypeId");
	dynamicView.addAlias("WEPA", "fromDate");
	dynamicView.addAlias("WEPA", "thruDate");
	dynamicView.addAlias("WEPA", "statusId");
	dynamicView.addAlias("WEPA", "ownerId");
	dynamicView.addAlias("WEPA", "emplTeamId");
	dynamicView.addAlias("WEPA", "businessUnit");
	dynamicView.addViewLink("WE", "WEPA", Boolean.TRUE, ModelKeyMap.makeKeyMapList("workEffortId"));

	if (UtilValidate.isNotEmpty(domainType) && domainType.equals(DomainEntityType.CONTACT)) {
		dynamicView.addMemberEntity("WEC", "WorkEffortContact");
		dynamicView.addAlias("WEC", "contactPartyId","partyId",null, false,false,null);
		dynamicView.addViewLink("WE", "WEC", Boolean.TRUE, ModelKeyMap.makeKeyMapList("workEffortId"));
	}

	if (UtilValidate.isNotEmpty(conditionList)) {

		EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		
		//println ("condition------------->"+condition);
		
		openActivities =  EntityQuery.use(delegator).from(dynamicView).where(condition).queryCount();
	}

}

context.put("ab-activity", openActivities);






