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

if(segId.equals("LINE_EOW")){
	
	JSONArray xAxisData = new JSONArray();
	
	xAxisData.add("Mon");
	xAxisData.add("Tue");
	xAxisData.add("Wed");
	xAxisData.add("Thu");
	xAxisData.add("Fri");
	xAxisData.add("Sat");
	xAxisData.add("Sun");
	
	context.put("xAxisData", xAxisData);
	request.setAttribute("xAxisData", xAxisData);
	
	def timeperiod= "lastWeek"
	def linenum = 1
	
	 cstmt = con.prepareCall("{call intelekt_cnt_sr_by_sts_by_day_data_fetch(?, ?)}");
	 cstmt.setString(1, "lastweek");
	 cstmt.setInt(2, 1);
	 
	 JSONArray createdSrData = new JSONArray();
	 
	 createdSrData.add(0);
	 createdSrData.add(0);
	 createdSrData.add(0);
	 createdSrData.add(0);
	 createdSrData.add(0);
	 createdSrData.add(0);
	 createdSrData.add(0);

	 rs = cstmt.executeQuery();
	 if(rs !=null){
		 while (rs.next()) {
			 
			 count = rs.getString(1);
			 status = rs.getString(2)
			 day = rs.getString(3)
			 if(status.equalsIgnoreCase("SR_OPEN")){
				 if( day.equalsIgnoreCase("Monday")){
					 createdSrData[0] = count;
				 }else if( day.equalsIgnoreCase("Tuesday")){
					 createdSrData[1] = count;
				 }else if( day.equalsIgnoreCase("Wednesday")){
					 createdSrData[2] = count;
				 }else if( day.equalsIgnoreCase("Thursday")){
					 createdSrData[3] = count;
				 }else if( day.equalsIgnoreCase("Friday")){
					 createdSrData[4] = count;
				 }else if( day.equalsIgnoreCase("Satarday")){
					 createdSrData[5] = count;
				 }else if( day.equalsIgnoreCase("Sunday")){
					 createdSrData[6] = count;
				 }
				 
			 }
			 
		 }
		 
	 }
	 context.put("createdLineSrData", createdSrData);
	 request.setAttribute("createdLineSrData", createdSrData);
	 
	 
	 JSONArray closedLineSrData = new JSONArray();
	 
	 
	 closedLineSrData.add(0);
	 closedLineSrData.add(0);
	 closedLineSrData.add(0);
	 closedLineSrData.add(0);
	 closedLineSrData.add(0);
	 closedLineSrData.add(0);
	 closedLineSrData.add(0);
	 closedLineSrData.add(0);
	 closedLineSrData.add(0);
	 closedLineSrData.add(0);
   
	 cstmt = con.prepareCall("{call intelekt_cnt_sr_by_sts_by_day_data_fetch(?, ?)}");
	 cstmt.setString(1, "lastweek");
	 cstmt.setInt(2, 2);
	 rs = cstmt.executeQuery();
	 if(rs !=null){
		 while (rs.next()) {
			 
			 count = rs.getString(1);
			 status = rs.getString(2)
			 day = rs.getString(3)
			 
			 if(status.equalsIgnoreCase("SR_CLOSED")){
				 if( day.equalsIgnoreCase("Monday")){
					 closedLineSrData[0] = count;
				 }else if( day.equalsIgnoreCase("Tuesday")){
					 closedLineSrData[1] = count;
				 }else if( day.equalsIgnoreCase("Wednesday")){
					 closedLineSrData[2] = count;
				 }else if( day.equalsIgnoreCase("Thursday")){
					 closedLineSrData[3] = count;
				 }else if( day.equalsIgnoreCase("Friday")){
					 closedLineSrData[4] = count;
				 }else if( day.equalsIgnoreCase("Satarday")){
					 closedLineSrData[5] = count;
				 }else if( day.equalsIgnoreCase("Sunday")){
					 closedLineSrData[6] = count;
				 }
	 
	 
			 }
		 
	 }
	 
 }
 
 context.put("closedLineSrData", closedLineSrData);
 request.setAttribute("closedLineSrData", closedLineSrData);
 JSONArray outStandSrData = new JSONArray();
 outStandSrData.add(0)
 outStandSrData.add(0)
 outStandSrData.add(0)
 outStandSrData.add(0)
 outStandSrData.add(0)
 outStandSrData.add(0)
 outStandSrData.add(0)
 cstmt = con.prepareCall("{call intelekt_cnt_sr_by_sts_by_day_data_fetch(?, ?)}");
 cstmt.setString(1, "lastweek");
 cstmt.setInt(2, 3);
 rs = cstmt.executeQuery();
 if(rs !=null){
	 while (rs.next()) {
		 
		 sum = rs.getString(1);
		 day = rs.getString(3);
		 status = rs.getString(2);
		 
	   if(status.equalsIgnoreCase("OUTSTANDING")){
		 if( day.equalsIgnoreCase("Monday")){
			 outStandSrData[0] = sum;
		 }else if( day.equalsIgnoreCase("Tuesday")){
			 outStandSrData[1] = sum;
		 }else if( day.equalsIgnoreCase("Wednesday")){
			 outStandSrData[2] = sum;
		 }else if( day.equalsIgnoreCase("Thursday")){
			 outStandSrData[3] = sum;
		 }else if( day.equalsIgnoreCase("Friday")){
			 outStandSrData[4] = sum;
		 }else if( day.equalsIgnoreCase("Tuesday")){
			 outStandSrData[1] = sum;
		 }else if( day.equalsIgnoreCase("Wednesday")){
			 outStandSrData[2] = sum;
		 }else if( day.equalsIgnoreCase("Thursday")){
			 outStandSrData[3] = sum;
		 }else if( day.equalsIgnoreCase("Friday")){
			 outStandSrData[4] = sum;
		 }else if( day.equalsIgnoreCase("Satarday")){
			 outStandSrData[5] = sum;
		 }else if( day.equalsIgnoreCase("Sunday")){
			 outStandSrData[6] = sum;
		 }
	   }
	 
}

}
context.put("outStandLineSrData", outStandSrData);
request.setAttribute("outStandLineSrData", outStandSrData);

  
}

sqlProcessor.close();
return "success";


			
			
		
			
			
			