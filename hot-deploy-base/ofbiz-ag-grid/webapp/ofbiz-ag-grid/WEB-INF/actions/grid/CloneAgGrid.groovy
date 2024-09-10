import org.fio.admin.portal.util.DataUtil
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

Map<String,Object> inputContext = new LinkedHashMap<String, Object>();

String instanceId=request.getParameter("instanceId");
String userId=request.getParameter("userId");
String role=request.getParameter("role");

if(UtilValidate.isNotEmpty(instanceId) && UtilValidate.isNotEmpty(userId) && UtilValidate.isNotEmpty(role)) {
	GenericValue gridUserPreferences = EntityQuery.use(delegator)
										.from("GridUserPreferences")
										.where("instanceId",instanceId,"userId",userId,"role",role)
										.queryFirst();
	if(UtilValidate.isNotEmpty(gridUserPreferences)) {
		inputContext.putAll(DataUtil.convertGenericValueToMap(delegator, gridUserPreferences));
		inputContext.put("instanceId", "");
	}
}
context.put("inputContext", inputContext);