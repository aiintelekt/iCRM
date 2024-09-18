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
	timePeriod = "THIS_MONTH";
}

if (timePeriod.equals("THIS_MONTH")){
	timePeriod = "thismonth";
} else if (timePeriod.equals("THIS_QUARTER")){
	timePeriod = "thisquarter";
} else if (timePeriod.equals("LAST_MONTH")){
	timePeriod = "lastmonth";
} else if (timePeriod.equals("LAST_QUARTER")){
	timePeriod = "lastquarter";
}

JSONArray xAxisData = new JSONArray();

if (timePeriod.equals("thismonth") || timePeriod.equals("lastmonth")) {
	
} else {
	
}

if (timePeriod.equals("thismonth") || timePeriod.equals("lastmonth")) {
	xAxisData.add("Week1");
	xAxisData.add("Week2");
	xAxisData.add("Week3");
	xAxisData.add("Week4");
} else {
	xAxisData.add("Week1");
	xAxisData.add("Week2");
	xAxisData.add("Week3");
	xAxisData.add("Week4");
	xAxisData.add("Week5");
	xAxisData.add("Week6");
	xAxisData.add("Week7");
	xAxisData.add("Week8");
	xAxisData.add("Week9");
	xAxisData.add("Week10");
	xAxisData.add("Week11");
	xAxisData.add("Week12");
}

JSONArray createdActivityData = new JSONArray();

if (timePeriod.equals("thismonth") || timePeriod.equals("lastmonth")) {
	createdActivityData.add(0);
	createdActivityData.add(0);
	createdActivityData.add(0);
	createdActivityData.add(0);
} else {
	createdActivityData.add(0);
	createdActivityData.add(0);
	createdActivityData.add(0);
	createdActivityData.add(0);
	createdActivityData.add(0);
	createdActivityData.add(0);
	createdActivityData.add(0);
	createdActivityData.add(0);
	createdActivityData.add(0);
	createdActivityData.add(0);
	createdActivityData.add(0);
	createdActivityData.add(0);
}

JSONArray completedActivityData = new JSONArray();

if (timePeriod.equals("thismonth") || timePeriod.equals("lastmonth")) {
	completedActivityData.add(0);
	completedActivityData.add(0);
	completedActivityData.add(0);
	completedActivityData.add(0);
} else {
	completedActivityData.add(0);
	completedActivityData.add(0);
	completedActivityData.add(0);
	completedActivityData.add(0);
	completedActivityData.add(0);
	completedActivityData.add(0);
	completedActivityData.add(0);
	completedActivityData.add(0);
	completedActivityData.add(0);
	completedActivityData.add(0);
	completedActivityData.add(0);
	completedActivityData.add(0);
}

JSONArray outstandingActivityData = new JSONArray();

if (timePeriod.equals("thismonth") || timePeriod.equals("lastmonth")) {
	outstandingActivityData.add(0);
	outstandingActivityData.add(0);
	outstandingActivityData.add(0);
	outstandingActivityData.add(0);
} else {
	outstandingActivityData.add(0);
	outstandingActivityData.add(0);
	outstandingActivityData.add(0);
	outstandingActivityData.add(0);
	outstandingActivityData.add(0);
	outstandingActivityData.add(0);
	outstandingActivityData.add(0);
	outstandingActivityData.add(0);
	outstandingActivityData.add(0);
	outstandingActivityData.add(0);
	outstandingActivityData.add(0);
	outstandingActivityData.add(0);
}

SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
Connection con = (Connection)sqlProcessor.getConnection();
CallableStatement cstmt = null;
ResultSet rs = null;

JSONObject chartData =new JSONObject();
chartData.put("xAxisData", xAxisData);

cstmt = con.prepareCall("{call intelekt_cnt_task_cummulative_of_EOW_data_fetch(?, ?)}");
cstmt.setString(1, timePeriod);
cstmt.setInt(2, 1);
rs = cstmt.executeQuery();

if(rs !=null){
try{ 
	while (rs.next()) {
		
		int i = 0;	
		count = rs.getString(1);
		status = rs.getString(2);
		day = rs.getString(3);
		
		if(UtilValidate.isNotEmpty(status) && UtilValidate.isNotEmpty(day)) {
			if(status.equalsIgnoreCase("IA_OPEN")){
				createdActivityData[i] = count;
			}
	    }
	    i++;
	}
	
	chartData.put("createdActivityData", createdActivityData);
} catch (Exception e) {
}	

}

cstmt = con.prepareCall("{call intelekt_cnt_task_cummulative_of_EOW_data_fetch(?, ?)}");
cstmt.setString(1, timePeriod);
cstmt.setInt(2, 2);
rs = cstmt.executeQuery();

if(rs !=null){
try{ 
	while (rs.next()) {
	
		int i = 0;	
		count = rs.getString(1);
		status = rs.getString(2);
		day = rs.getString(3);
		
		if(UtilValidate.isNotEmpty(status) && UtilValidate.isNotEmpty(day)) {
			if(status.equalsIgnoreCase("IA_MCOMPLETED")){
				completedActivityData[i] = count;
			}
	    }
	    i++;
	}
	
	chartData.put("completedActivityData", completedActivityData);
} catch (Exception e) {
}	

}

cstmt = con.prepareCall("{call intelekt_cnt_task_cummulative_of_EOW_data_fetch(?, ?)}");
cstmt.setString(1, timePeriod);
cstmt.setInt(2, 3);
rs = cstmt.executeQuery();

if(rs !=null){
try{ 
	while (rs.next()) {
	
		int i = 0;	
		count = rs.getString(1);
		status = rs.getString(2);
		day = rs.getString(3);
		
		if(UtilValidate.isNotEmpty(status) && UtilValidate.isNotEmpty(day)) {
			if(status.equalsIgnoreCase("OUTSTANDING")){
				outstandingActivityData[i] = count;
			}
	    }
	    i++;
	}
	
	chartData.put("outstandingActivityData", outstandingActivityData);
} catch (Exception e) {
}	

}

request.setAttribute("task_by_week_chart_data", chartData);

rs.close();
con.close();
sqlProcessor.close();

return "success";
