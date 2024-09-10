
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilDateTime;

import org.fio.campaign.common.UtilCommon;
import org.fio.campaign.util.StringUtil;

import java.util.*;
import java.util.Calendar;
import org.ofbiz.base.util.UtilNumber;


import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.entity.jdbc.SQLProcessor;
import java.sql.ResultSet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.GenericValue;

import java.sql.Timestamp;
import java.text.SimpleDateFormat

import org.fio.campaign.util.CampaignUtil;
import org.fio.crm.util.DataHelper;
import org.fio.campaign.util.LoginFilterUtil;
import org.fio.homeapps.util.ResponseUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.fio.campaign.events.AjaxEvents;


userLogin = request.getAttribute("userLogin");
String userLoginId = userLogin.get("partyId");
context.ownerUserLoginId=userLoginId;

Calendar now = Calendar.getInstance();

now.add(Calendar.MONTH,-6);



day=now.get(Calendar.DAY_OF_MONTH);
year=now.get(Calendar.YEAR);
month=now.get(Calendar.MONTH);
sixMonthsDate="";
month=month+1;
if(month<10) {
	monthStr= String.valueOf(month)
	monthStr="0"+monthStr;
	month = Integer.parseInt(monthStr);
	sixMonthsDate=day+"-"+monthStr+"-"+year;
	
}else {
	sixMonthsDate=day+"-"+month+"-"+year;
}

context.sixMonthsDate=sixMonthsDate;


ResultSet rs = null;
SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
Map<String, Object> dashboardBarContext = new LinkedHashMap<String, Object>();

long openActivities = 0;
long completedActivities = 0;

if(UtilValidate.isNotEmpty(userLogin)) {
	String loginPartyId = userLogin.getString("partyId");
	
	if("admin".equals(loginPartyId)) {
		String openActivitiesSql = "SELECT COUNT(*) as 'openActivities' FROM work_effort WHERE `CURRENT_STATUS_ID`='IA_OPEN'";
		String completedActivitiesSql = "SELECT COUNT(*) as 'completedActivities' FROM work_effort WHERE `CURRENT_STATUS_ID`='IA_MCOMPLETED'";
		rs = sqlProcessor.executeQuery(openActivitiesSql);
		if (rs != null) {
			while (rs.next()) {
				openActivities = rs.getLong("openActivities");
			}
		}
		rs = sqlProcessor.executeQuery(completedActivitiesSql);
		if (rs != null) {
			while (rs.next()) {
				completedActivities = rs.getLong("completedActivities");
			}
		}
		
	} else {
		String openActivitiesSql = "SELECT COUNT(*) as 'openActivities' FROM work_effort WHERE `CURRENT_STATUS_ID`='IA_OPEN' AND (prim_owner_id IN (SELECT GET_PARTY_ID_USER_LOGIN(party_id) FROM `empl_position_fulfillment` WHERE `PARTY_ID_TO`='"+loginPartyId+"') OR prim_owner_id= GET_PARTY_ID_USER_LOGIN('"+loginPartyId+"'))";
		String completedActivitiesSql = "SELECT COUNT(*) as 'completedActivities' FROM work_effort WHERE `CURRENT_STATUS_ID`='IA_MCOMPLETED' AND (prim_owner_id IN (SELECT GET_PARTY_ID_USER_LOGIN(party_id) FROM `empl_position_fulfillment` WHERE `PARTY_ID_TO`='"+loginPartyId+"') OR prim_owner_id= GET_PARTY_ID_USER_LOGIN('"+loginPartyId+"'))";
		rs = sqlProcessor.executeQuery(openActivitiesSql);
		if (rs != null) {
			while (rs.next()) {
				openActivities = rs.getLong("openActivities");
			}
		}
		rs = sqlProcessor.executeQuery(completedActivitiesSql);
		if (rs != null) {
			while (rs.next()) {
				completedActivities = rs.getLong("completedActivities");
			}
		}
		
	}
}
dashboardBarContext.put("activity-open", openActivities);
dashboardBarContext.put("activity-completed", completedActivities);
context.put("dashboardBarContext", dashboardBarContext);
