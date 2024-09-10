import java.util.ArrayList;
import java.util.HashMap;

import javolution.util.FastList;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;



partyId = request.getParameter("partyId");
partyClassificationListFinal = new ArrayList();
partyClassificationGroup = delegator.findByAnd("PartyClassificationGroup",UtilMisc.toMap("partyClassificationTypeId","CUST_CLASSIFICATION"),null,false);
context.put("partyClassificationGroup",partyClassificationGroup);

EntityCondition DateCondition = EntityCondition.makeCondition(EntityOperator.OR,
	EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()),
	EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
	

EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
	EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
	DateCondition);

	  
partyClassificationList = delegator.findList("PartyClassification",condition,null,null, null, false);

context.put("partyClassification",partyClassificationList);

context.put("partyId",partyId);