import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

String permissionId = request.getParameter("permissionId");

GenericValue permissionGv = EntityQuery.use(delegator).from("SecurityPermission").where("permissionId",permissionId).queryOne();
if(UtilValidate.isNotEmpty(permissionGv)) {
	Map<String,Object> data = new HashMap<String,Object>();
	data.put("permissionId", permissionGv.get("permissionId"));
	data.put("description", permissionGv.get("description"));
	context.put("securityPermission", data)
}