import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

String appBarId = request.getParameter("appBarId");
String appBarTypeId = request.getParameter("appBarTypeId");
if(UtilValidate.isNotEmpty(appBarId) && UtilValidate.isNotEmpty(appBarTypeId)) {
	List<GenericValue> appBarElementList = EntityQuery.use(delegator).from("AppBarElements").where("appBarId",appBarId,"appBarTypeId",appBarTypeId).orderBy("appBarElementSeqNum ASC").queryList();
	if(UtilValidate.isNotEmpty(appBarElementList)) {
		Map<String, Object> appBarContext = new HashMap<String, Object>();
		for(GenericValue appBarElement : appBarElementList) {
			appBarContext.put(appBarElement.getString("elementKey"), 0);
		}
		context.put("appBarContext", appBarContext);
	}
}