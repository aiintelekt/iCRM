import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.opentaps.common.util.UtilCommon;

import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.util.EntityUtil;

delegator = request.getAttribute("delegator");

layoutCond = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLogin.getString("userLoginId"))
        			//EntityCondition.makeCondition("screenLayoutTypeId", EntityOperator.EQUALS, "DASHBOARD")
				);
        	
screenLayoutList = delegator.findList("ScreenLayoutTest", layoutCond, null, UtilMisc.toList("screenLayoutId ASC"), null, false);
context.put("screenLayoutList", screenLayoutList);
