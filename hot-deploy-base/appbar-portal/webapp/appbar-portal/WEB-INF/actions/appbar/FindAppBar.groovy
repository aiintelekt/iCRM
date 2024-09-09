import org.fio.admin.portal.util.DataUtil
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

List<GenericValue> appBarTypeList = EntityQuery.use(delegator).from("AppBarType").queryList();
if(UtilValidate.isNotEmpty(appBarTypeList)) {
	context.put("appBarTypeList",DataUtil.getMapFromGeneric(appBarTypeList, "appBarTypeId", "appBarTypeName", false));
}

List<GenericValue> appBarStatusList = EntityQuery.use(delegator).from("Enumeration").where("enumTypeId","APP_BAR_STATUS").orderBy("sequenceId ASC").queryList();
if(UtilValidate.isNotEmpty(appBarStatusList)) {
	context.put("appBarStatusList",DataUtil.getMapFromGeneric(appBarStatusList, "enumCode", "description", false));
}
