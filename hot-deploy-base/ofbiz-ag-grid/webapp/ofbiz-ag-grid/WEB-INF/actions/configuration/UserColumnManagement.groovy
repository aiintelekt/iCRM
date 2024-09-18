import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

String gridInstanceId = request.getParameter("gridInstanceId");
String gridUserId = request.getParameter("gridUserId");

println ("gridInstanceId-->"+gridInstanceId+"---->"+gridUserId);
GenericValue gridPref = EntityQuery.use(delegator).from("GridUserPreferences")
						.where("instanceId", gridInstanceId, "role","USER", "userId",gridUserId)
						.queryFirst();

println ("gridPref-->"+gridPref);
if(UtilValidate.isNotEmpty(gridPref)) {
	String description = UtilValidate.isNotEmpty(gridPref.getString("description")) ? gridPref.getString("description") : gridPref.getString("name") ;
	context.put("gridName", description);
}