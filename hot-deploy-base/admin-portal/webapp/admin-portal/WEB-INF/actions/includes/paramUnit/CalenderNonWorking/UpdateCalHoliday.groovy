import org.fio.admin.portal.constant.AdminPortalConstant.DateTimeTypeConstant;
import org.fio.admin.portal.util.DataUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;

import java.text.SimpleDateFormat;
import java.util.function.Function;
import java.util.stream.Collectors;


delegator = request.getAttribute("delegator");

inputContext = new LinkedHashMap<String, Object>();

String holidayConfigId=request.getParameter("holidayConfigId");

if(UtilValidate.isNotEmpty(holidayConfigId)) {
	GenericValue holidayConfig = EntityQuery.use(delegator).from("TechDataHolidayConfig").where("holidayConfigId",holidayConfigId).queryFirst();
	println("holidayConfig =="+holidayConfig );
	if(UtilValidate.isNotEmpty(holidayConfig)) {
		//context.put("holidayConfig", holidayConfig );
		
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		
		inputContext.put("holidayConfigId", holidayConfig.get("holidayConfigId"));
		
		inputContext.put("nonWorkingDate", UtilValidate.isNotEmpty(holidayConfig.get("holidayDate")) ? df.format(holidayConfig.get("holidayDate")) : null);
		inputContext.put("nonWorkingDate1", UtilValidate.isNotEmpty(holidayConfig.get("holidayDate")) ? df.format(holidayConfig.get("holidayDate")) : null);
		
		inputContext.put("holidayDescription", holidayConfig.get("holidayDescription"));
		inputContext.put("type", holidayConfig.get("type"));
		inputContext.put("status", holidayConfig.get("status"));
						
	}
}

context.put("inputContext", inputContext);
