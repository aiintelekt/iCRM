import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

List < GenericValue > marketingCampaign = delegator.findList("MarketingCampaign", null, UtilMisc.toSet("marketingCampaignId", "campaignName"), UtilMisc.toList("campaignName"), null, false);
context.put("marketingCampaignList", marketingCampaign);

List < GenericValue > callNumberEnumerationList = delegator.findList("Enumeration", EntityCondition.makeCondition('enumTypeId', EntityOperator.EQUALS, "CALL_STATUS"), UtilMisc.toSet("sequenceId"), UtilMisc.toList("sequenceId"), null, false);
context.put("callNumberList", EntityUtil.getFieldListFromEntityList(callNumberEnumerationList, "sequenceId", true));

List < GenericValue > partyRelationship =  delegator.findByAnd("PartyRelationship",UtilMisc.toMap("partyRelationshipTypeId","ASSIGNED_TO"),UtilMisc.toList("fromDate DESC"),false);
csrPartyListId = EntityUtil.getFieldListFromEntityList(partyRelationship, "partyIdTo", true);
if(UtilValidate.isNotEmpty(csrPartyListId)) {
	List < GenericValue > personList = delegator.findList("Person", EntityCondition.makeCondition("partyId", EntityOperator.IN, csrPartyListId), null, UtilMisc.toList("firstName"), null, false);
	context.put("personList", personList)
}
