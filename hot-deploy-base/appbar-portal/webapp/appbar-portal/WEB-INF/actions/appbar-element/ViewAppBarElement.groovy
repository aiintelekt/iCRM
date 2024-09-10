import org.fio.admin.portal.util.DataUtil
import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.*;

delegator = request.getAttribute("delegator");
appBarId= request.getParameter("appBarId");
appBarTypeId= request.getParameter("appBarTypeId");
appBarElementId= request.getParameter("appBarElementId");
inputContext = new LinkedHashMap<String, Object>();
GenericValue appBarElements = EntityQuery
					.use(delegator)
					.from("AppBarElements")
					.where("appBarId",appBarId,"appBarTypeId",appBarTypeId,"appBarElementId", appBarElementId)
					.queryFirst();
if(UtilValidate.isNotEmpty(appBarElements)) {
	inputContext = DataUtil.convertGenericValueToMap(delegator, appBarElements);
}
context.put("inputContext", inputContext);
GenericValue appBar = EntityQuery
						.use(delegator)
						.from("AppBar")
						.where("appBarId",appBarId,"appBarTypeId",appBarTypeId)
						.queryFirst();
if(UtilValidate.isNotEmpty(appBar)) {
	context.put("appBarName", appBar.getString("appBarName"));
}