import org.ofbiz.base.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.ofbiz.base.util.Debug;

import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilDateTime;
import java.util.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.jdbc.SQLProcessor;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.ResultSet;

currentDate = UtilDateTime.timeStampToString(UtilDateTime.nowTimestamp(), "YYYY-MM-dd", TimeZone.getDefault(), null);

String timePeriod = request.getParameter("timePeriod");

if (UtilValidate.isEmpty(timePeriod)) {
	timePeriod = "THIS_WEEK";
}

if(timePeriod.equals("LAST_WEEK")){
	timePeriod = "lastweek";
} else if (timePeriod.equals("THIS_WEEK")){
	timePeriod = "thisweek";
} else if (timePeriod.equals("LAST_MONTH")){
	timePeriod = "lastmonth";
} else if (timePeriod.equals("THIS_MONTH")){
	timePeriod = "thismonth";
} else if (timePeriod.equals("LAST_QUARTER")){
	timePeriod = "lastquarter";
} else if (timePeriod.equals("THIS_QUARTER")){
	timePeriod = "thisquarter";
}

JSONArray xAxisData = new JSONArray();

JSONArray taskTypeData = new JSONArray();

JSONArray appointmentTypeData = new JSONArray();

SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
Connection con = (Connection)sqlProcessor.getConnection();
CallableStatement cstmt = null;
ResultSet rs = null;
JSONObject chartData = new JSONObject();

cstmt = con.prepareCall("{call intelekt_duration_IA_by_type_by_user_data_fetch(?)}");
		
cstmt.setString(1, timePeriod);
rs = cstmt.executeQuery();
println("timePeriod> "+timePeriod);
if(rs !=null){
try{ 

	dataList = new LinkedHashMap();
	int i = 0;
	while (rs.next()) {
		count = rs.getString(1);
		type = rs.getString(2);
		user = rs.getString(3);
		
		if(UtilValidate.isNotEmpty(type) && UtilValidate.isNotEmpty(user)) {
			
			if (type.equals("Task") || type.equals("Appointment")) {
				if (dataList.containsKey(user)) {
					data = dataList.get(user);
					if(type.equalsIgnoreCase("Task")){
						data.put("taskTypeCount", count);
					} else if(type.equalsIgnoreCase("Appointment")){
						data.put("apntTypeCount", count);
					}
				} else {
					data = new LinkedHashMap();
					if(type.equalsIgnoreCase("Task")){
						data.put("taskTypeCount", count);
					} else if(type.equalsIgnoreCase("Appointment")){
						data.put("apntTypeCount", count);
					}
				}
				
				dataList.put(user, data);
			}
	    }
	}
	
	for (String user : dataList.keySet()) {
		data = dataList.get(user);
		xAxisData.add(user);
		taskTypeData.add(UtilValidate.isNotEmpty(data.get("taskTypeCount")) ? data.get("taskTypeCount") : 0);
		appointmentTypeData.add(UtilValidate.isNotEmpty(data.get("apntTypeCount")) ? data.get("apntTypeCount") : 0);
	} 
	
	chartData.put("xAxisData", xAxisData);
	chartData.put("taskTypeData", taskTypeData);
	chartData.put("appointmentTypeData", appointmentTypeData);
} catch (Exception e) {
	e.printStackTrace();
}			

request.setAttribute("duration_activity_chart_data", chartData);

}

rs.close();
con.close();
sqlProcessor.close();

return "success";
