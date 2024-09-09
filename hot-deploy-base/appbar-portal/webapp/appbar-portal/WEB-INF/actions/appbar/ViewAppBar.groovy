import org.fio.admin.portal.util.DataUtil
import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.*;

delegator = request.getAttribute("delegator");
appBarId= request.getParameter("appBarId");
appBarTypeId= request.getParameter("appBarTypeId");

inputContext = new LinkedHashMap<String, Object>();
GenericValue appBar = EntityQuery
					.use(delegator)
					.from("AppBar")
					.where("appBarId",appBarId,"appBarTypeId",appBarTypeId)
					.queryFirst();
if(UtilValidate.isNotEmpty(appBar)) {
	inputContext = DataUtil.convertGenericValueToMap(delegator, appBar);
}
context.put("appBar", appBar);
context.put("inputContext", inputContext);
