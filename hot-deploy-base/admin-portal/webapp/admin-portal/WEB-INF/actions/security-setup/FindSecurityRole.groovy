import org.fio.admin.portal.util.DataHelper
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;

List<GenericValue> roleType = EntityQuery.use(delegator).select("roleTypeId","description").from("RoleType").where("parentTypeId","DBS_ROLE").queryList();
if(UtilValidate.isNotEmpty(roleType)) {
	context.put("roles", DataHelper.getDropDownOptions(roleType, "roleTypeId", "description"));
}