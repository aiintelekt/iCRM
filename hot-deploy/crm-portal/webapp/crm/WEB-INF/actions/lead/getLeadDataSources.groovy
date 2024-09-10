import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import org.ofbiz.party.party.PartyHelper;
import java.util.Date;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.util.EntityFindOptions;
import org.fio.crm.util.DataHelper;

dataSourceconditions = new ArrayList();
dataSourceconditions.add(new EntityExpr("disable", EntityOperator.NOT_EQUAL, "Y"));
dataSourceconditions.add(new EntityExpr("dataSourceTypeId", EntityOperator.EQUALS, "LEAD_GENERATION"));

dataSourceconditions = new EntityConditionList(dataSourceconditions, EntityOperator.AND);
dataSourceList = delegator.findList("DataSource", dataSourceconditions, null, UtilMisc.toList("description ASC"), null, false);
context.put("dataSourceList", DataHelper.getDropDownOptions(dataSourceList, "dataSourceId", "description"));

ownershipList = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "PARTY_OWNERSHIP"), UtilMisc.toList("sequenceId ASC"), false);
context.put("ownershipList", DataHelper.getDropDownOptions(ownershipList, "enumId", "description"));

industryEnumList = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "PARTY_INDUSTRY"), UtilMisc.toList("sequenceId ASC"), false);
context.put("industryEnumList", DataHelper.getDropDownOptions(industryEnumList, "enumId", "description"));

EntityCondition classifiCondition =
    EntityCondition.makeCondition("partyClassificationTypeId", EntityOperator.EQUALS, "LEAD_CLASSIFICATION");


partyClassificationGroup = delegator.findList("PartyClassificationGroup", classifiCondition, null, null, null, false);

context.put("PartyClassificationGroup", partyClassificationGroup);