import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;

import java.text.SimpleDateFormat
import java.util.HashMap;

import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import java.util.LinkedHashMap;

import org.fio.admin.portal.constant.ResponseConstants
import org.fio.admin.portal.constant.AdminPortalConstant.DateTimeTypeConstant
import org.fio.admin.portal.util.DataHelper;
import org.fio.admin.portal.util.DataUtil

delegator = request.getAttribute("delegator");

/*List<GenericValue> apiLogs = delegator.findByAnd("OfbizApiLog", null, null, false);
context.put("apiLogIds", DataHelper.getDropDownOptions(apiLogs, "ofbizApiLogId", "ofbizApiLogId"));
context.put("apiservices", DataHelper.getDropDownOptions(apiLogs, "serviceName", "serviceName"));*/

List<GenericValue> channels = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId" , "SR_CHANNEL"), null, false);
context.put("channels", DataHelper.getDropDownOptions(channels, "enumId", "description"));

List<GenericValue> systemNames = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId" , "SYSTEM_NAME"), null, false);
context.put("systemNames", DataHelper.getDropDownOptions(systemNames, "enumId", "description"));

serviceNames =EntityQuery.use(delegator).select("serviceName").from("OfbizApiLog").distinct().queryList();
context.put("serviceNames", DataHelper.getDropDownOptions(serviceNames, "serviceName", "serviceName"));

searchCriteria = request.getParameter("searchCriteria");
if(UtilValidate.isEmpty(searchCriteria)) {
	/*List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();
	Set<String> fieldsToSelect = new TreeSet<String>();
	SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	fieldsToSelect.add("ofbizApiLogId");
	fieldsToSelect.add("requestedData");
	fieldsToSelect.add("responsedData");
	fieldsToSelect.add("clientApiLogId");
	fieldsToSelect.add("serviceName");
	fieldsToSelect.add("systemName");
	fieldsToSelect.add("orgId");
	fieldsToSelect.add("msgId");
	fieldsToSelect.add("requestedTime");
	fieldsToSelect.add("responsedTime");
	fieldsToSelect.add("responseStatus");
	fieldsToSelect.add("responseCode");
	List<GenericValue> ofbizApiLogs = EntityQuery.use(delegator).select(fieldsToSelect).from("OfbizApiLog").orderBy("-lastUpdatedTxStamp").queryList();
	if(ofbizApiLogs != null && ofbizApiLogs.size() > 0)
	{
		for(GenericValue apiLog : ofbizApiLogs) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("ofbizApiLogId", apiLog.getString("ofbizApiLogId"));
			data.put("requestJson", ResponseConstants.Json); // apiLog.getString("requestedData")
			data.put("responseJson", ResponseConstants.Json); //apiLog.getString("responsedData")
			data.put("channelId", apiLog.getString("clientApiLogId"));
			data.put("serviceName", apiLog.getString("serviceName"));
			data.put("systemName", apiLog.getString("systemName"));
			data.put("orgId", apiLog.getString("orgId"));
			data.put("msgId", apiLog.getString("msgId"));
			data.put("responseCode", apiLog.getString("responseCode"));
			String  requestedTime = apiLog.getString("requestedTime");
			String  responsedTime = apiLog.getString("responsedTime");
			if(UtilValidate.isNotEmpty(requestedTime)) {
				requestedTime = DataUtil.convertDateTimestamp(requestedTime, new SimpleDateFormat("dd/MM/yyyy hh:mm"), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
			}
			if(UtilValidate.isNotEmpty(responsedTime)) {
				responsedTime = DataUtil.convertDateTimestamp(responsedTime, new SimpleDateFormat("dd/MM/yyyy hh:mm"), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
			}
			
			requestedTime = DataUtil.convertDateTimestamp(requestedTime, df, DateTimeTypeConstant.DATE, DateTimeTypeConstant.STRING);
			responsedTime = DataUtil.convertDateTimestamp(responsedTime, df, DateTimeTypeConstant.DATE, DateTimeTypeConstant.STRING);
			data.put("requestedTime", requestedTime);
			data.put("responsedTime",  responsedTime);
			//addStatues.add(apiLog.getString("responseStatus"));
			data.put("status", apiLog.getString("responseStatus"));
			results.add(data);
		}
	}*/
}else {
	context.put("searchCriteria", searchCriteria);
}