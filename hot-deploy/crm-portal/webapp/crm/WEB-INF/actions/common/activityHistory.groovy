import java.sql.Timestamp;
import java.util.HashMap
import java.util.List;
import java.util.Map

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.base.conversion.JSONConverters.ListToJSON;
import org.ofbiz.base.lang.JSON;

//Display WorkEffort Details
partyIdWorkEffort = request.getParameter("partyId");
List activityHistoryList = [];
if (UtilValidate.isNotEmpty(partyIdWorkEffort)) {
    Set < String > fieldsToSelect = UtilMisc.toSet("workEffortId", "workEffortTypeId", "workEffortName", "currentStatusId", "estimatedStartDate", "estimatedCompletionDate");
    fieldsToSelect.add("workEffortPurposeTypeId");
    fieldsToSelect.add("sequenceNum");
    fieldsToSelect.add("actualStartDate");
    fieldsToSelect.add("actualCompletionDate");
    fieldsToSelect.add("createdDate");
    fieldsToSelect.add("createdByUserLogin");
    fieldsToSelect.add("partyId");
    List < String > orderByFields = ["actualCompletionDate DESC", "workEffortId"];
    List < String > pendingStatus = UtilMisc.toList("TASK_SCHEDULED", "TASK_CONFIRMED", "TASK_ON_HOLD", "EVENT_SCHEDULED", "EVENT_CONFIRMED", "EVENT_ON_HOLD");;
    List < EntityCondition > activityHistoryCondList = UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyIdWorkEffort));
    for (Iterator < String > iter = pendingStatus.iterator(); iter.hasNext();) {
        activityHistoryCondList.add(EntityCondition.makeCondition("currentStatusId", EntityOperator.NOT_EQUAL, iter.next()));
    }
    activityHistoryCondList.add(EntityUtil.getFilterByDateExpr());

    EntityCondition activityHistoryCond = EntityCondition.makeCondition(activityHistoryCondList, EntityOperator.AND);
    List < GenericValue > workEffortAndPartyAssign = delegator.findList("WorkEffortAndPartyAssign", activityHistoryCond, fieldsToSelect, orderByFields, null, false);
    if (workEffortAndPartyAssign != null && workEffortAndPartyAssign.size() > 0) {
        for (GenericValue activityHistoryGV: workEffortAndPartyAssign) {
            Map activityHistoryMap = new HashMap();
            //String workEffortTypeId = "";
            String workEffortPurposeTypeId = "";
            String actualStartDate = "";
            String actualCompletionDate = "";
            String workEffortName = "";
            //String currentStatusId = "";
            String content = "";
            communicationEventWorkEff = EntityQuery.use(delegator).from("CommunicationEventWorkEff").where("workEffortId", activityHistoryGV.getString("workEffortId")).queryFirst();
            if (communicationEventWorkEff != null && communicationEventWorkEff.size() > 0) {
                String communicationEventId = communicationEventWorkEff.getString("communicationEventId");
                GenericValue communicationEvent = delegator.findOne("CommunicationEvent", UtilMisc.toMap("communicationEventId", communicationEventId), false);
                if (communicationEvent != null && communicationEvent.size() > 0) {
                    content = communicationEvent.getString("content");
                }
            }

            /*workEffortType = EntityQuery.use(delegator).from("WorkEffortType").where("workEffortTypeId", activityHistoryGV.getString("workEffortTypeId")).queryOne();
            if (workEffortType != null && workEffortType.size() > 0) {
                workEffortTypeId = workEffortType.get("description");
            }*/
            workEffortPurposeType = EntityQuery.use(delegator).from("WorkEffortPurposeType").where("workEffortPurposeTypeId", activityHistoryGV.getString("workEffortPurposeTypeId")).queryOne();
            if (workEffortPurposeType != null && workEffortPurposeType.size() > 0) {
                workEffortPurposeTypeId = workEffortPurposeType.get("description");
            }
            /*statusItem = EntityQuery.use(delegator).from("StatusItem").where("statusId", activityHistoryGV.getString("currentStatusId")).queryOne();
            if (statusItem != null && statusItem.size() > 0) {
               currentStatusId = statusItem.get("description");
            }*/
            activityHistoryMap.putAll(activityHistoryGV);
            activityHistoryMap.put("content", content);
            //activityHistoryMap.put("workEffortTypeId", workEffortTypeId);
            activityHistoryMap.put("workEffortPurposeTypeId", workEffortPurposeTypeId);
            //activityHistoryMap.put("currentStatusId", currentStatusId);
            activityHistoryList.add(activityHistoryMap)

        }
        //println("activityHistoryList=======" + activityHistoryList);
        context.put("activityHistoryList", activityHistoryList);
		ListToJSON listToJSON = new ListToJSON();
		JSON json = listToJSON.convert(activityHistoryList);
		context.put("activityHistoryListStr", json.toString());
    }
}