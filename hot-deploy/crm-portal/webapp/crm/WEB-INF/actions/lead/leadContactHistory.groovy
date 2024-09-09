import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;
import javolution.util.FastList;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.conversion.JSONConverters.ListToJSON;
import org.ofbiz.base.lang.JSON;

leadContactHistoryList = FastList.newInstance();
partyId = parameters.get("partyId");

/*leadStatusHistory = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "LEAD_STATUS_HISTORY"), UtilMisc.toList("sequenceId"), false);
context.put("leadStatusHistoryList", leadStatusHistory);*/
leadStatusHistory = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "LEAD_STATUS"), UtilMisc.toList("sequenceId"), false);
context.put("leadStatusHistoryList", leadStatusHistory);

if (UtilValidate.isNotEmpty(partyId)) {
    List<GenericValue> leadContactHistory = delegator.findByAnd("LeadContactHistory", ["partyId": partyId], ["createdStamp DESC"], false);
    if(UtilValidate.isNotEmpty(leadContactHistory)){
       GenericValue getLeadStatus = EntityUtil.getFirst(leadContactHistory);
       context.put("currentStatus",getLeadStatus.getString("leadContactStageIdTo"));
    }
    for (GenericValue leadContactHistoryGen : leadContactHistory) {
        Map orderMap = new HashMap();
        String statusFrom = leadContactHistoryGen.getString("leadContactStageIdFrom");
        String statusTo = leadContactHistoryGen.getString("leadContactStageIdTo");
        String descriptionFrom ="";
        String descriptionTo ="";
        String modifiedBy ="";
        if (UtilValidate.isNotEmpty(statusFrom)) {
            GenericValue leadContactHistoryFrom = delegator.findOne("LeadContactStage", ["leadContactStageId": statusFrom], false);
            if(UtilValidate.isNotEmpty(leadContactHistoryFrom)){
                descriptionFrom = leadContactHistoryFrom.getString("description")
            }
            orderMap.put("leadContactStageIdFrom", descriptionFrom);
        }
        if (UtilValidate.isNotEmpty(statusTo)) {
            GenericValue leadContactHistoryTo = delegator.findOne("LeadContactStage", ["leadContactStageId": statusTo], false);
            if(UtilValidate.isNotEmpty(leadContactHistoryTo)){
                descriptionTo = leadContactHistoryTo.getString("description")
            }
            orderMap.put("leadContactStageIdTo", descriptionTo);
        }
        userLogin = from("UserLogin").where("userLoginId", leadContactHistoryGen.getString("modifiedBy")).queryOne();
        if(UtilValidate.isNotEmpty(userLogin)){
            person = from("Person").where("partyId", userLogin.getString("partyId")).queryOne();
            modifiedBy = person.getString("firstName")+" "+person.getString("lastName")
        }
        orderMap.put("modifiedBy", modifiedBy);
        orderMap.put("createdStamp", leadContactHistoryGen.getTimestamp("createdStamp"));
        leadContactHistoryList.add(orderMap);
    }
    
    //leadDndStatus = delegator.findByAnd("PartyAttribute", UtilMisc.toMap("partyId",partyId,"attrName","DND_STATUS"), null, false);
    leadDndStatus = delegator.findByAnd("DndAuditLogDetails", UtilMisc.toMap("partyId",partyId), null, false);
    context.put("leadDndStatus", leadDndStatus);
    //System.out.println("leadDndStatus"+leadDndStatus);
	ListToJSON listToJSON = new ListToJSON();
	JSON json = listToJSON.convert(leadDndStatus);
	context.put("leadDndStatusListStr", json.toString());
	
	List<Map<String, Object>> rmHistoryList = new ArrayList<Map<String, Object>>();	
	String pkCombinedValueText = partyId + "::" + partyId;
	List<GenericValue> leadReassignStatus = delegator.findByAnd("ValidationAuditLog", UtilMisc.toMap("validationAuditType","VAT_RM_REASSIGN", "pkCombinedValueText", pkCombinedValueText), UtilMisc.toList("createdStamp DESC"), false);
	
	if(leadReassignStatus != null && leadReassignStatus.size() > 0) {
		for (GenericValue leadReassignStatusGV : leadReassignStatus) {
			rmMap = [:];
			String reassignFromPartyId = leadReassignStatusGV.getString("oldValueText");
			String reassignToPartyId = leadReassignStatusGV.getString("newValueText");
			String createdByUserLoginId = leadReassignStatusGV.getString("changedByInfo");
			String rmFromName = "";
			String rmToName = "";
			String rmUserLoginName = "";
			if (UtilValidate.isNotEmpty(reassignFromPartyId)) {
				GenericValue personFrom = delegator.findOne("Person", ["partyId": reassignFromPartyId], false);
				if(UtilValidate.isNotEmpty(personFrom)){
					rmFromName = personFrom.getString("firstName");
					if(UtilValidate.isNotEmpty(personFrom.getString("lastName"))) {
						rmFromName = rmFromName +" "+ personFrom.getString("lastName");
					}
				}
			}
			if (UtilValidate.isNotEmpty(reassignToPartyId)) {
				GenericValue personTo = delegator.findOne("Person", ["partyId": reassignToPartyId], false);
				if(UtilValidate.isNotEmpty(personTo)){
					rmToName = personTo.getString("firstName");
					if(UtilValidate.isNotEmpty(personTo.getString("lastName"))) {
						rmToName = rmToName +" "+ personTo.getString("lastName");
					}
				}
			}
			if (UtilValidate.isNotEmpty(createdByUserLoginId)) {
				GenericValue userLoginReassign = delegator.findOne("UserLogin", ["userLoginId": createdByUserLoginId], false);
				if(userLoginReassign != null && userLoginReassign.size() > 0) {
				GenericValue userLoginPerson = delegator.findOne("Person", ["partyId": userLoginReassign.getString("partyId")], false);
				if(UtilValidate.isNotEmpty(userLoginPerson)){
					rmUserLoginName = userLoginPerson.getString("firstName");
					if(UtilValidate.isNotEmpty(userLoginPerson.getString("lastName"))) {
						rmUserLoginName = rmUserLoginName +" "+ userLoginPerson.getString("lastName");
					}
				}
				}
			}
			rmMap.put("rmFromName", rmFromName);
			rmMap.put("rmToName", rmToName);
			rmMap.put("rmUserLoginName", rmUserLoginName);
			rmMap.put("modifiedDate", leadReassignStatusGV.getString("createdStamp"));
			rmHistoryList.add(rmMap);
		}
	}
	println("rmHistoryList> "+rmHistoryList);
	context.put("rmHistoryList", rmHistoryList);
}
context.put("leadContactHistory", leadContactHistoryList);

