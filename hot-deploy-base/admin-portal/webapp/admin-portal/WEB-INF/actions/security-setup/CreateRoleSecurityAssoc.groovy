import org.fio.admin.portal.util.DataUtil
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

List<GenericValue> roleList = EntityQuery.use(delegator).from("RoleType").queryList();
if(UtilValidate.isNotEmpty(roleList)) {
	context.put("roleList", DataUtil.getMapFromGeneric(roleList, "roleTypeId", "description", false));
}

List<GenericValue> securityGroupList = EntityQuery.use(delegator).from("SecurityGroup").queryList();
if(UtilValidate.isNotEmpty(securityGroupList)) {
	context.put("securityGroupList", DataUtil.getMapFromGeneric(securityGroupList, "groupId", "description", false));
}