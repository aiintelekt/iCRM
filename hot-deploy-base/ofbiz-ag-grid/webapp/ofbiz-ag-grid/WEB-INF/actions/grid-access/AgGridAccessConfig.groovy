import org.fio.admin.portal.util.DataHelper
import org.fio.admin.portal.util.DataUtil
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

List<GenericValue> securityGroupList = EntityQuery.use(delegator).from("SecurityGroup").queryList();
if(UtilValidate.isNotEmpty(securityGroupList)) {
	context.put("groupList",DataUtil.getMapFromGeneric(securityGroupList, "groupId", "description", false));
}

List<GenericValue> gridInstanceList = EntityQuery.use(delegator).select("instanceId","name").from("GridUserPreferences").where("userId","admin","role","ADMIN").queryList();
if(UtilValidate.isNotEmpty(gridInstanceList)) {
	context.put("gridInstanceList",DataUtil.getMapFromGeneric(gridInstanceList, "instanceId", "name", false));
}