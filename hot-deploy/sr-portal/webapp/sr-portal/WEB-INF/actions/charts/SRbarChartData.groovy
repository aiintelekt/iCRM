import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.Debug;
import java.sql.Timestamp;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.ofbiz.entity.jdbc.SQLProcessor;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

delegator = request.getAttribute("delegator");
String segId = request.getParameter("segmentCode");

SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
Connection con = (Connection)sqlProcessor.getConnection();
CallableStatement cstmt = null;
ResultSet rs = null;
JSONArray pieChartData = new JSONArray();

String timePeriodStr = null;

if(segId.equals("BAR_LAST_WEEK")){
	timePeriodStr = "lastWeek"
}else if (segId.equals("BAR_THIS_WEEK")){
	timePeriodStr = "thisWeek"
}else if (segId.equals("BAR_LAST_MONTH")){
	timePeriodStr = "lastMonth"
}else if (segId.equals("BAR_THIS_MONTH")){
	timePeriodStr = "thisMonth"
}else if (segId.equals("BAR_LAST_QUARTER")){
	timePeriodStr = "lastQuarter"
}else if (segId.equals("BAR_THIS_QUARTER")){
	timePeriodStr = "thisQuarter"
}
if(timePeriodStr !=null){
	
	JSONArray xAxisData = new JSONArray();
	
	xAxisData.add("Mon");
	xAxisData.add("Tue");
	xAxisData.add("Wed");
	xAxisData.add("Thu");
	xAxisData.add("Fri");
	xAxisData.add("Sat");
	xAxisData.add("Sun");
	
	
	context.put("xAxisData", xAxisData.toString());
		
	System.out.println("Conection is established 1");
	def timeperiod= timePeriodStr
	def linenum = 1
	 cstmt = con.prepareCall("{call intelekt_cnt_sr_created_over_period_data_fetch(?)}");
	cstmt.setString(1, timeperiod);
	 rs = cstmt.executeQuery();
	JSONArray openSrData = new JSONArray();
	openSrData.add(0);
	openSrData.add(0);
	openSrData.add(0);
	openSrData.add(0);
	openSrData.add(0);
	openSrData.add(0);
	openSrData.add(0);
	JSONArray closedSrData = new JSONArray();
	
	closedSrData.add(0);
	closedSrData.add(0);
	closedSrData.add(0);
	closedSrData.add(0);
	closedSrData.add(0);
	closedSrData.add(0);
	closedSrData.add(0);
	
	if(rs !=null){
		while (rs.next()) {
			
			day = rs.getString(3);
			status = rs.getString(2)
			count = rs.getString(1)
			
			if(status.equalsIgnoreCase("SR_OPEN")){
				
				if( day.equalsIgnoreCase("Monday")){
					openSrData[0] = count;
				}else if( day.equalsIgnoreCase("Tuesday")){
					openSrData[1] = count;
				}else if( day.equalsIgnoreCase("Wednesday")){
					openSrData[2] = count;
				}else if( day.equalsIgnoreCase("Thursday")){
					openSrData[3] = count;
				}else if( day.equalsIgnoreCase("Friday")){
					openSrData[4] = count;
				}else if( day.equalsIgnoreCase("Saturday")){
					openSrData[5] = count;
				}else if( day.equalsIgnoreCase("Sunday")){
					openSrData[6] = count;
				}
			}else if(status.equalsIgnoreCase("SR_CLOSED")){
				if(day != null){
				if( day.equalsIgnoreCase("Monday")){
					closedSrData[0] = count;
				}else if( day.equalsIgnoreCase("Tuesday")){
					closedSrData[1] = count;
				}else if( day.equalsIgnoreCase("Wednesday")){
					closedSrData[2] = count;
				}else if( day.equalsIgnoreCase("Thursday")){
					closedSrData[3] = count;
				}else if( day.equalsIgnoreCase("Friday")){
					closedSrData[4] = count;
				}else if( day.equalsIgnoreCase("Saturday")){
					closedSrData[5] = count;
				}else if( day.equalsIgnoreCase("Sunday")){
					closedSrData[6] = count;
				}
			}
			}
		}
		context.put("openSrData", openSrData.toString());
		context.put("closedSrData", closedSrData.toString());
		request.setAttribute("xAxisData", xAxisData);
		request.setAttribute("openSrData", openSrData);
		request.setAttribute("closedSrData", closedSrData);
		
		
	 }
			
}			
		
sqlProcessor.close();
return "success";

			
			