import org.fio.admin.portal.util.DataHelper
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery

String groupId = request.getParameter("groupId");
context.put("groupId", groupId);
GenericValue groupGv = EntityQuery.use(delegator).from("SecurityGroup").where("groupId",groupId).queryOne();
if(UtilValidate.isNotEmpty(groupGv)) {
	Map<String,Object> data = new HashMap<String,Object>();
	data.put("groupId", groupGv.get("groupId"));
	data.put("securityTypeId", groupGv.get("securityTypeId"));
	data.put("description", groupGv.get("description"));
	context.put("securityGroup", data)
}
EntityCondition securityTypeCondition = EntityCondition.makeCondition(EntityOperator.OR,
                    EntityCondition.makeCondition("isDisabled", EntityOperator.EQUALS, "N"),
                    EntityCondition.makeCondition("isDisabled", EntityOperator.EQUALS, null)
                );
List<GenericValue> securityType = EntityQuery.use(delegator).select("securityTypeId","description").from("SecurityType").where(securityTypeCondition).queryList();
if(UtilValidate.isNotEmpty(securityType)) {
	context.put("securityTypes", DataHelper.getDropDownOptions(securityType, "securityTypeId", "description"));
}