import org.ofbiz.base.util.UtilValidate;
import org.fio.homeapps.util.DataUtil
import org.ofbiz.base.util.*;
import java.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

GenericValue company = EntityQuery.use(delegator).from("PartyGroup").where(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company")).cache().queryFirst();
if (UtilValidate.isNotEmpty(company)) {
	context.put("companyName", company.getString("groupName"));
}
