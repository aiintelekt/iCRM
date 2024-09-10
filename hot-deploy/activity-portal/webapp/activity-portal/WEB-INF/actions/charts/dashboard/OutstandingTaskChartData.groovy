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
	timePeriod = currentDate;
}

SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
Connection con = (Connection)sqlProcessor.getConnection();
CallableStatement cstmt = null;
ResultSet rs = null;
JSONArray chartData = new JSONArray();

cstmt = con.prepareCall("{call intelekt_cnt_IA_by_usr_data_fetch(?)}");
		
cstmt.setString(1, timePeriod);
rs = cstmt.executeQuery();

if(rs !=null){
	while (rs.next()) {
		JSONObject data =new JSONObject();
			
		countOfIa = rs.getString(1);
		user = rs.getString(2);
		
		if(UtilValidate.isNotEmpty(user)){
			
			data.put("name", user);
			data.put("value", countOfIa);
			
			chartData.add(data);
	    }
		
	}
			
	request.setAttribute("outstanding_task_chart_data", chartData);
}

rs.close();
con.close();
sqlProcessor.close();

return "success";
