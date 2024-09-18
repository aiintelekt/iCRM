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

String timePeriodLineStr = null;

if (segId.equals("LINE_CM_EOW_LASTMONTH")){
	timePeriodLineStr = "lastmonth"
}else if (segId.equals("LINE_CM_EOW_THISMONTH")){
	timePeriodLineStr = "thismonth"
}else if (segId.equals("LINE_CM_EOW_LASTQTR")){
	timePeriodLineStr = "lastquarter"
}else if (segId.equals("LINE_CM_EOW_THISQTR")){
	timePeriodLineStr = "thisquarter"
}
if(timePeriodLineStr !=null){
		JSONArray xAxisData = new JSONArray();
		
			
			def timeperiod= timePeriodLineStr
			def linenum = 1
			
			/*#CALL intelekt_cnt_sr_by_sts_by_day_data_fetch('lastweek',1);
			CALL intelekt_cnt_sr_by_sts_by_day_data_fetch('lastweek',2);
			#CALL intelekt_cnt_sr_by_sts_by_day_data_fetch('lastweek',3);*/
			
			cstmt = con.prepareCall("{call intelekt_cnt_sr_by_status_by_week_data_fetch(?, ?)}");
			cstmt.setString(1, timePeriodLineStr);
			cstmt.setInt(2, 1);
			
			JSONArray createdSrData = new JSONArray();
			JSONArray closedLineSrData = new JSONArray();
			JSONArray outStandSrData = new JSONArray();
			
			rs = cstmt.executeQuery();
			if(rs !=null){
				int i = 0;
				while (rs.next()) {
					
					
					count = rs.getString(1);
					status = rs.getString(2)
					day = rs.getString(3)
					
					if(status.equalsIgnoreCase("SR_OPEN")){
						
							createdSrData[i] = count;
							closedLineSrData[i]= 0;
							outStandSrData[i] = 0;
					}
					
					i++;
					xAxisData.add("Week"+i);
					
				}
				
			}
			context.put("xAxisData", xAxisData);
			request.setAttribute("xAxisData", xAxisData);
			context.put("createdLineSrData", createdSrData);
			request.setAttribute("createdLineSrData", createdSrData);
			
			cstmt = con.prepareCall("{call intelekt_cnt_sr_by_status_by_week_data_fetch(?, ?)}");
			cstmt.setString(1, timePeriodLineStr);
			cstmt.setInt(2, 2);
			
			rs = cstmt.executeQuery();
			if(rs !=null){
				
				int i = 0;
				while (rs.next()) {
					
					
					count = rs.getString(1);
					status = rs.getString(2)
					day = rs.getString(3)
					
					if(status.equalsIgnoreCase("SR_CLOSED")){
												
							closedLineSrData[i] = count;
					}					
					i++;
				}
			}
		
		
			context.put("closedLineSrData", closedLineSrData);
			request.setAttribute("closedLineSrData", closedLineSrData);
			
		
		cstmt = con.prepareCall("{call intelekt_cnt_sr_by_status_by_week_data_fetch(?, ?)}");
		cstmt.setString(1, timePeriodLineStr);
		cstmt.setInt(2, 3);
		rs = cstmt.executeQuery();
		
		if(rs !=null){
			int i = 0;
			while (rs.next()) {
					
					
					count = rs.getString(1);					
					status = rs.getString(2);					
					day = rs.getString(3);
															
					if(status.equalsIgnoreCase("OUTSTANDING")){
						
							outStandSrData[i] = count;
					}
					1++;
					
			}
			
		}
		
		context.put("outStandLineSrData", outStandSrData);
		request.setAttribute("outStandLineSrData", outStandSrData);
		
  }
  
			
			
  sqlProcessor.close();
  return "success";
  
			
			
			
			
			
			
		
			
			
			