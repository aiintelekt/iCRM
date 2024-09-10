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

xAxisData.add("Mon");
xAxisData.add("Tue");
xAxisData.add("Wed");
xAxisData.add("Thu");
xAxisData.add("Fri");
xAxisData.add("Sat");
xAxisData.add("Sun");

request.setAttribute("xAxisData", xAxisData);

JSONArray openActivityData = new JSONArray();

openActivityData.add(0);
openActivityData.add(0);
openActivityData.add(0);
openActivityData.add(0);
openActivityData.add(0);
openActivityData.add(0);
openActivityData.add(0);

JSONArray completedActivityData = new JSONArray();

completedActivityData.add(0);
completedActivityData.add(0);
completedActivityData.add(0);
completedActivityData.add(0);
completedActivityData.add(0);
completedActivityData.add(0);
completedActivityData.add(0);

SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
Connection con = (Connection)sqlProcessor.getConnection();
CallableStatement cstmt = null;
ResultSet rs = null;
JSONObject chartData = new JSONObject();

cstmt = con.prepareCall("{call intelekt_cnt_task_by_status_data_fetch(?)}");
		
cstmt.setString(1, timePeriod);
rs = cstmt.executeQuery();
println("timePeriod> "+timePeriod);
if(rs !=null){
try{ 
	while (rs.next()) {
		
		count = rs.getString(1);
		status = rs.getString(2);
		day = rs.getString(3);
		
		if(UtilValidate.isNotEmpty(status) && UtilValidate.isNotEmpty(day)) {
		
			if(status.equalsIgnoreCase("IA_OPEN")){
				if(day.equalsIgnoreCase("Monday")){
					openActivityData[0] = count;
				} else if(day.equalsIgnoreCase("Tuesday")){
					openActivityData[1] = count;
				} else if(day.equalsIgnoreCase("Wednesday")){
					openActivityData[2] = count;
				} else if(day.equalsIgnoreCase("Thursday")){
					openActivityData[3] = count;
				} else if(day.equalsIgnoreCase("Friday")){
					openActivityData[4] = count;
				} else if(day.equalsIgnoreCase("Satarday")){
					openActivityData[5] = count;
				} else if(day.equalsIgnoreCase("Sunday")){
					openActivityData[6] = count;
				}
			} else if(status.equalsIgnoreCase("IA_MCOMPLETED")){
				if(day.equalsIgnoreCase("Monday")){
					completedActivityData[0] = count;
				} else if(day.equalsIgnoreCase("Tuesday")){
					completedActivityData[1] = count;
				} else if(day.equalsIgnoreCase("Wednesday")){
					completedActivityData[2] = count;
				} else if(day.equalsIgnoreCase("Thursday")){
					completedActivityData[3] = count;
				} else if(day.equalsIgnoreCase("Friday")){
					completedActivityData[4] = count;
				} else if(day.equalsIgnoreCase("Satarday")){
					completedActivityData[5] = count;
				} else if(day.equalsIgnoreCase("Sunday")){
					completedActivityData[6] = count;
				}
			}
	    }
	}
	
	chartData.put("openActivityData", openActivityData);
	chartData.put("completedActivityData", completedActivityData);
} catch (Exception e) {
	e.printStackTrace();
}			

request.setAttribute("task_by_day_chart_data", chartData);

}

rs.close();
con.close();
sqlProcessor.close();

return "success";
