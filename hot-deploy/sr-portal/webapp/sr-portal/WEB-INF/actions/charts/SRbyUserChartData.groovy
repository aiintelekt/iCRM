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

SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
Connection con = (Connection)sqlProcessor.getConnection();
CallableStatement cstmt = null;
ResultSet rs = null;
JSONArray pieChartData = new JSONArray();

currentDate = UtilDateTime.timeStampToString(UtilDateTime.nowTimestamp(), "YYYY-MM-dd", TimeZone.getDefault(), null);
	
	
	 cstmt = con.prepareCall("{call intelekt_cnt_sr_by_usr_data_fetch(?)}");
	
	 cstmt.setString(1, currentDate);
	 rs = cstmt.executeQuery();
	 
	 if(rs !=null){
		 while (rs.next()) {
			 
			 JSONObject pieJsonObj =new JSONObject();
			 
			 	value = "";
				name = "";
			    
				 value = rs.getString(1)
				 name = rs.getString(2);
				 				
				 if(UtilValidate.isNotEmpty(name)){
				 pieJsonObj.put("name", name);
				 }
				 if(UtilValidate.isNotEmpty(value)){
				 pieJsonObj.put("value", value);
				 }
			 
	 
			 if(UtilValidate.isNotEmpty(pieJsonObj)){
			 pieChartData.add(pieJsonObj);
			 }
			 
		 }
		 
		 context.put("pieChartData", pieChartData);
		 request.setAttribute("pieChartVal", pieChartData);
	 
	}
	
	


sqlProcessor.close();
return "success";
