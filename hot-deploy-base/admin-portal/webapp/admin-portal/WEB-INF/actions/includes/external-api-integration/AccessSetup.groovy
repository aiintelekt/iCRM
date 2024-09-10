import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;

import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import java.util.LinkedHashMap;
import org.fio.admin.portal.util.DataHelper;
import org.fio.admin.portal.util.DataUtil

import java.text.SimpleDateFormat;

import org.fio.admin.portal.constant.AdminPortalConstant.DateTimeTypeConstant

delegator = request.getAttribute("delegator");

List<GenericValue> applnNames = delegator.findByAnd("ChannelAccess", null, null, false);
context.put("systemName", DataHelper.getDropDownOptions(applnNames, "applicationName", "applicationName"));
context.put("authMethod", DataHelper.getDropDownOptions(applnNames, "channelAccessType", "channelAccessType"));

/*searchCriteria = request.getParameter("searchCriteria");
println("===searchCriteria===="+searchCriteria);
if (UtilValidate.isEmpty(searchCriteria)) {
	List < Map < String, Object >> results = new ArrayList < Map < String, Object >> ();
        //Set<String> addStatues = new HashSet<String>();
        Set < String > fieldsToSelect = new TreeSet < String > ();
        fieldsToSelect.add("applicationName");
        fieldsToSelect.add("channelAccessUrl");
        fieldsToSelect.add("clientName");
        fieldsToSelect.add("password");
        fieldsToSelect.add("channelAccessType");
        fieldsToSelect.add("description");
        fieldsToSelect.add("lastUpdatedTxStamp");
        //fieldsToSelect.add("status");
        List < GenericValue > channelAccesses = EntityQuery.use(delegator).select(fieldsToSelect).from("ChannelAccess").orderBy("-lastUpdatedTxStamp").queryList();
        if (channelAccesses != null && channelAccesses.size() > 0) {
            for (GenericValue channelAccess: channelAccesses) {
                Map < String, Object > data = new HashMap < String, Object > ();
                data.put("systemName", channelAccess.getString("applicationName"));
                data.put("urlAccess", channelAccess.getString("channelAccessUrl"));
                data.put("userId", channelAccess.getString("clientName"));
                data.put("password", channelAccess.getString("password"));
                data.put("authMethod", channelAccess.getString("channelAccessType"));
                data.put("description", channelAccess.getString("description"));
                data.put("lastModified", DataUtil.timeStampToDate(channelAccess.getTimestamp("lastUpdatedTxStamp")));
                //addStatues.add(apiLog.getString("responseStatus"));
                data.put("status", "");
                results.add(data);
            }
        }
        
} else {
 	println('==searchCriteria==='+searchCriteria);
    context.put("searchCriteria", searchCriteria);
}*/