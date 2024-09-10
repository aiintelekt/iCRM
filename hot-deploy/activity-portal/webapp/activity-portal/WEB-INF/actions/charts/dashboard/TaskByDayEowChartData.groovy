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

String timePeriod = "lastweek";

JSONArray xAxisData = new JSONArray();

xAxisData.add("Mon");
xAxisData.add("Tue");
xAxisData.add("Wed");
xAxisData.add("Thu");
xAxisData.add("Fri");
xAxisData.add("Sat");
xAxisData.add("Sun");

JSONArray createdActivityData = new JSONArray();

createdActivityData.add(0);
createdActivityData.add(0);
createdActivityData.add(0);
createdActivityData.add(0);
createdActivityData.add(0);
createdActivityData.add(0);
createdActivityData.add(0);

JSONArray completedActivityData = new JSONArray();

completedActivityData.add(0);
completedActivityData.add(0);
completedActivityData.add(0);
completedActivityData.add(0);
completedActivityData.add(0);
completedActivityData.add(0);
completedActivityData.add(0);

JSONArray outstandingActivityData = new JSONArray();

outstandingActivityData.add(0);
outstandingActivityData.add(0);
outstandingActivityData.add(0);
outstandingActivityData.add(0);
outstandingActivityData.add(0);
outstandingActivityData.add(0);
outstandingActivityData.add(0);

SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
Connection con = (Connection)sqlProcessor.getConnection();
CallableStatement cstmt = null;
ResultSet rs = null;

JSONObject chartData =new JSONObject();
chartData.put("xAxisData", xAxisData);

cstmt = con.prepareCall("{call intelekt_cnt_task_as_of_eow_data_fetch(?, ?)}");
cstmt.setString(1, timePeriod);
cstmt.setInt(2, 1);
rs = cstmt.executeQuery();

if(rs !=null){
try{ 
	while (rs.next()) {
			
		count = rs.getString(1);
		status = rs.getString(2);
		day = rs.getString(3);
		
		if(UtilValidate.isNotEmpty(status) && UtilValidate.isNotEmpty(day)) {
		
			if(status.equalsIgnoreCase("IA_OPEN")){
				if(day.equalsIgnoreCase("Monday")){
					createdActivityData[0] = count;
				} else if( day.equalsIgnoreCase("Tuesday")){
					createdActivityData[1] = count;
				} else if( day.equalsIgnoreCase("Wednesday")){
					createdActivityData[2] = count;
				} else if( day.equalsIgnoreCase("Thursday")){
					createdActivityData[3] = count;
				} else if( day.equalsIgnoreCase("Friday")){
					createdActivityData[4] = count;
				} else if( day.equalsIgnoreCase("Satarday")){
					createdActivityData[5] = count;
				} else if( day.equalsIgnoreCase("Sunday")){
					createdActivityData[6] = count;
				}
			}
	    }
	}
	
	chartData.put("createdActivityData", createdActivityData);
} catch (Exception e) {
}	

}

cstmt = con.prepareCall("{call intelekt_cnt_task_as_of_eow_data_fetch(?, ?)}");
cstmt.setString(1, timePeriod);
cstmt.setInt(2, 2);
rs = cstmt.executeQuery();

if(rs !=null){
try{ 
	while (rs.next()) {
			
		count = rs.getString(1);
		status = rs.getString(2);
		day = rs.getString(3);
		
		if(UtilValidate.isNotEmpty(status) && UtilValidate.isNotEmpty(day)) {
		
			if(status.equalsIgnoreCase("IA_MCOMPLETED")){
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
			
			chartData.put("completedActivityData", completedActivityData);
	    }
	}
} catch (Exception e) {
}	

}

cstmt = con.prepareCall("{call intelekt_cnt_task_as_of_eow_data_fetch(?, ?)}");
cstmt.setString(1, timePeriod);
cstmt.setInt(2, 3);
rs = cstmt.executeQuery();

if(rs !=null){
try{ 
	while (rs.next()) {
			
		count = rs.getString(1);
		status = rs.getString(2);
		day = rs.getString(3);
		
		if(UtilValidate.isNotEmpty(status) && UtilValidate.isNotEmpty(day)) {
		
			if(status.equalsIgnoreCase("OUTSTANDING")){
				if(day.equalsIgnoreCase("Monday")){
					outstandingActivityData[0] = count;
				} else if(day.equalsIgnoreCase("Tuesday")){
					outstandingActivityData[1] = count;
				} else if(day.equalsIgnoreCase("Wednesday")){
					outstandingActivityData[2] = count;
				} else if(day.equalsIgnoreCase("Thursday")){
					outstandingActivityData[3] = count;
				} else if(day.equalsIgnoreCase("Friday")){
					outstandingActivityData[4] = count;
				} else if(day.equalsIgnoreCase("Satarday")){
					outstandingActivityData[5] = count;
				} else if(day.equalsIgnoreCase("Sunday")){
					outstandingActivityData[6] = count;
				}
			}
			
			chartData.put("outstandingActivityData", outstandingActivityData);
	    }
	}
} catch (Exception e) {
}	

}

request.setAttribute("task_by_day_eow_chart_data", chartData);

rs.close();
con.close();
sqlProcessor.close();

return "success";
