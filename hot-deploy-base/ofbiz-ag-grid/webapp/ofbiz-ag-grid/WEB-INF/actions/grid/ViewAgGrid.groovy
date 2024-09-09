import org.fio.admin.portal.util.DataUtil
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

import com.google.gson.Gson
import com.google.gson.GsonBuilder

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
		context.put("gridUserPreferences",gridUserPreferences);
		inputContext.putAll(DataUtil.convertGenericValueToMap(delegator, gridUserPreferences));
		/*
		String jsonString = gridUserPreferences.getString("gridOptionsJsString");
		Map jsonMap = DataUtil.convertToMap(jsonString);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		if(UtilValidate.isNotEmpty(jsonString))
			inputContext.put("gridOptionsJsString", gson.toJson(jsonString));		
		*/
	}
}

context.put("inputContext", inputContext);