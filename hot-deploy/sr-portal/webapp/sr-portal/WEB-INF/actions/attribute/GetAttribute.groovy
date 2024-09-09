import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;

import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.GenericValue;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.condition.EntityExpr;

import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.base.util.UtilDateTime;

exprBldr = new org.ofbiz.entity.condition.EntityConditionBuilder();

srNumber = parameters.get("srNumber");

context.put("srNumber", srNumber);

conditionsList = [];
conditionsList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, srNumber));
conditionsList.add(EntityCondition.makeCondition("channelId", EntityOperator.NOT_EQUAL, null));

mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);

attrList = from("CustRequestAttribute").select("channelId").where(mainConditons).orderBy("sequenceNumber").queryList();	
List<String> channelIdLst = EntityUtil.getFieldListFromEntityList(attrList, "channelId", true)
println("channelIdLst: "+channelIdLst);
context.put("channelIdLst", channelIdLst);

context.put("attrList", attrList);


