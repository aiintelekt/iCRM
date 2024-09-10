
import java.sql.CallableStatement
import java.sql.Connection
import java.sql.ResultSet;

import org.fio.admin.portal.util.DataUtil
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.util.EntityQuery;

userLogin = request.getAttribute("userLogin");
String userLoginPartyId = userLogin.get("partyId");
context.ownerUserLoginId=userLoginPartyId;

Calendar now = Calendar.getInstance();
now.add(Calendar.MONTH,-6);
day=now.get(Calendar.DAY_OF_MONTH);
year=now.get(Calendar.YEAR);
month=now.get(Calendar.MONTH);
sixMonthsDate="";
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

long openSrCount = 0;
long inprogressSrCount = 0;
long pendingSrCount = 0;
long cancelledSrCount = 0;
long closedSrCount = 0;
long atRiskSrCount = 0;
long overDueSrCount = 0;
long highPrioritySrCount = 0;

if(UtilValidate.isNotEmpty(userLogin)) {
	String loginPartyId = userLogin.getString("partyId");
	
	/*if("admin".equals(loginPartyId)) {
		String openSrSql = "SELECT COUNT(*) as 'openSrCount' FROM `cust_request` WHERE  `STATUS_ID` ='SR_OPEN'";
		String inprogressSrSql = "SELECT COUNT(*) as 'inprogressSrCount' FROM `cust_request` WHERE  `STATUS_ID` ='SR_IN_PROGRESS'";
		String pendingSrSql = "SELECT COUNT(*) as 'pendingSrCount' FROM `cust_request` WHERE  `STATUS_ID` ='SR_PENDING'";
		String cancelledSrSql = "SELECT COUNT(*) as 'cancelledSrCount' FROM `cust_request` WHERE  `STATUS_ID` ='SR_CANCELLED'";
		String closedSrSql = "SELECT COUNT(*) as 'closedSrCount' FROM `cust_request` WHERE `STATUS_ID` ='SR_CLOSED'";
		String atRiskSrSql = "SELECT COUNT(*) as 'atRiskCount' FROM cust_request cr INNER JOIN cust_request_supplementory crs ON cr.CUST_REQUEST_ID = crs.CUST_REQUEST_ID WHERE cr.status_id NOT IN ('SR_CLOSED','SR_CANCELLED') AND crs.PRE_ESCALATION_DATE < NOW() AND crs.COMMIT_DATE > NOW()";
		String overDueSrSql = "SELECT COUNT(*) as 'overdueCount' FROM cust_request cr INNER JOIN cust_request_supplementory crs ON cr.CUST_REQUEST_ID = crs.CUST_REQUEST_ID WHERE cr.status_id NOT IN ('SR_CLOSED','SR_CANCELLED') AND crs.COMMIT_DATE < NOW()";
		
		rs = sqlProcessor.executeQuery(openSrSql);
		if (rs != null) {
			while (rs.next()) {
				openSrCount = rs.getLong("openSrCount");
			}
		}
		rs = sqlProcessor.executeQuery(inprogressSrSql);
		if (rs != null) {
			while (rs.next()) {
				inprogressSrCount = rs.getLong("inprogressSrCount");
			}
		}
		rs = sqlProcessor.executeQuery(pendingSrSql);
		if (rs != null) {
			while (rs.next()) {
				pendingSrCount = rs.getLong("pendingSrCount");
			}
		}
		rs = sqlProcessor.executeQuery(cancelledSrSql);
		if (rs != null) {
			while (rs.next()) {
				cancelledSrCount = rs.getLong("cancelledSrCount");
			}
		}
		rs = sqlProcessor.executeQuery(closedSrSql);
		if (rs != null) {
			while (rs.next()) {
				closedSrCount = rs.getLong("closedSrCount");
			}
		}

		rs = sqlProcessor.executeQuery(atRiskSrSql);
		if (rs != null) {
			while (rs.next()) {
				atRiskSrCount = rs.getLong("atRiskCount");
			}
		}
		
		rs = sqlProcessor.executeQuery(overDueSrSql);
		if (rs != null) {
			while (rs.next()) {
				overDueSrCount = rs.getLong("overdueCount");
			}
		}
		
	} else {*/
		//String openSrSql = "SELECT COUNT(*) as 'openSrCount' FROM `cust_request` WHERE `STATUS_ID` ='SR_OPEN' AND (RESPONSIBLE_PERSON IN (SELECT GET_PARTY_ID_USER_LOGIN(party_id) FROM `empl_position_fulfillment` WHERE `PARTY_ID_TO`='"+loginPartyId+"') OR RESPONSIBLE_PERSON= GET_PARTY_ID_USER_LOGIN('"+loginPartyId+"'))";
		//String inprogressSrSql = "SELECT COUNT(*) as 'inprogressSrCount' FROM `cust_request` WHERE `STATUS_ID` ='SR_IN_PROGRESS' AND (RESPONSIBLE_PERSON IN (SELECT GET_PARTY_ID_USER_LOGIN(party_id) FROM `empl_position_fulfillment` WHERE `PARTY_ID_TO`='"+loginPartyId+"') OR RESPONSIBLE_PERSON= GET_PARTY_ID_USER_LOGIN('"+loginPartyId+"'))";
		//String pendingSrSql = "SELECT COUNT(*) as 'pendingSrCount' FROM `cust_request` WHERE `STATUS_ID` ='SR_PENDING' AND (RESPONSIBLE_PERSON IN  (SELECT GET_PARTY_ID_USER_LOGIN(party_id) FROM `empl_position_fulfillment` WHERE `PARTY_ID_TO`='"+loginPartyId+"') OR RESPONSIBLE_PERSON= GET_PARTY_ID_USER_LOGIN('"+loginPartyId+"'))";
		///String cancelledSrSql = "SELECT COUNT(*) as 'cancelledSrCount' FROM `cust_request` WHERE `STATUS_ID` ='SR_CANCELLED' AND (RESPONSIBLE_PERSON IN  (SELECT GET_PARTY_ID_USER_LOGIN(party_id) FROM `empl_position_fulfillment` WHERE `PARTY_ID_TO`='"+loginPartyId+"') OR RESPONSIBLE_PERSON= GET_PARTY_ID_USER_LOGIN('"+loginPartyId+"'))";
		//String closedSrSql = "SELECT COUNT(*) as 'closedSrCount' FROM `cust_request` WHERE `STATUS_ID` ='SR_CLOSED' AND (RESPONSIBLE_PERSON IN (SELECT GET_PARTY_ID_USER_LOGIN(party_id) FROM `empl_position_fulfillment` WHERE `PARTY_ID_TO`='"+loginPartyId+"') OR RESPONSIBLE_PERSON= GET_PARTY_ID_USER_LOGIN('"+loginPartyId+"'))";
		//String atRiskSrSql = "SELECT COUNT(*) as 'atRiskCount' FROM cust_request cr INNER JOIN cust_request_supplementory crs ON cr.CUST_REQUEST_ID = crs.CUST_REQUEST_ID WHERE cr.status_id NOT IN ('SR_CLOSED','SR_CANCELLED') AND crs.PRE_ESCALATION_DATE < NOW() AND crs.COMMIT_DATE > NOW() AND (RESPONSIBLE_PERSON IN (SELECT GET_PARTY_ID_USER_LOGIN(party_id) FROM `empl_position_fulfillment` WHERE `PARTY_ID_TO`='"+loginPartyId+"')  OR RESPONSIBLE_PERSON= GET_PARTY_ID_USER_LOGIN('"+loginPartyId+"'))";
		//String overDueSrSql = "SELECT COUNT(*) as 'overdueCount' FROM cust_request cr INNER JOIN cust_request_supplementory crs ON cr.CUST_REQUEST_ID = crs.CUST_REQUEST_ID WHERE cr.status_id NOT IN ('SR_CLOSED','SR_CANCELLED') AND crs.COMMIT_DATE < NOW() AND (RESPONSIBLE_PERSON IN (SELECT GET_PARTY_ID_USER_LOGIN(party_id) FROM `empl_position_fulfillment` WHERE `PARTY_ID_TO`='"+loginPartyId+"') OR RESPONSIBLE_PERSON= GET_PARTY_ID_USER_LOGIN('"+loginPartyId+"'))";
		/*
		String openSrSql = "SELECT COUNT(*) as 'openSrCount' FROM `cust_request` a INNER JOIN  `cust_request_party` b ON  a.cust_request_id=b.cust_request_id AND a.status_id NOT IN ('SR_CLOSED','SR_CANCELLED') AND b.party_id='"+loginPartyId+"'";
			
		String inprogressSrSql = "SELECT COUNT(*) as 'inprogressSrCount' FROM `cust_request` a INNER JOIN  `cust_request_party` b ON  a.cust_request_id=b.cust_request_id AND a.status_id in ('SR_RECEIVED','SR_HOLD_PER_SR_PRVDER','SR_HOLD_PER_TSM','SR_HOLD_UNTL_SPRNG','SR_OPEN') AND b.party_id='"+loginPartyId+"'";
		
		String pendingSrSql = "SELECT COUNT(*) as 'pendingSrCount' FROM cust_request a INNER JOIN  `cust_request_party` b ON  a.cust_request_id=b.cust_request_id AND a.status_id in ('SR_OPEN') AND b.party_id='"+loginPartyId+"'";
		
		String atRiskSrSql = "SELECT COUNT(DISTINCT a.cust_request_id) as 'atRiskCount' FROM cust_request a INNER JOIN  `cust_request_party` b ON  a.cust_request_id=b.cust_request_id AND a.status_id NOT IN ('SR_CLOSED','SR_CANCELLED') INNER JOIN cust_request_work_effort c ON a.cust_request_id = b.cust_request_id INNER JOIN work_effort d ON d.work_Effort_id = c.work_effort_id WHERE (DATE(a.last_modified_date) < DATE(NOW() - INTERVAL 3 WEEK) AND DATE(d.last_modified_date) < DATE(NOW() - INTERVAL 3 WEEK)) AND party_id ='"+loginPartyId+"'";
		
		String overDueSrSql = "SELECT COUNT(*) as 'overdueCount' FROM cust_request a INNER JOIN  `cust_request_party` b ON  a.cust_request_id=b.cust_request_id AND a.status_id IN ('SR_CLOSED','SR_CANCELLED') AND week(closed_by_date)=DATE((NOW()-INTERVAL 1 WEEK)) and year(a.closed_by_date) =year(now()) AND b.party_id='"+loginPartyId+"'";
		
		String cancelledSrSql = "SELECT COUNT(*) as 'cancelledSrCount' FROM cust_request a INNER JOIN  `cust_request_party` b ON  a.cust_request_id=b.cust_request_id AND a.status_id IN ('SR_CLOSED','SR_CANCELLED') AND  MONTH(a.closed_by_date)=MONTH((NOW()-INTERVAL 1 MONTH)) and year(a.closed_by_date) =year(now()) AND b.party_id='"+loginPartyId+"'";
		
		String highPrioritySrSql = "SELECT COUNT(DISTINCT a.cust_request_id) as 'highPrioritySrCount' FROM cust_request a INNER JOIN  `cust_request_party` b ON  a.cust_request_id=b.cust_request_id INNER JOIN enumeration c ON a.priority = c.enum_id AND  c.enum_type_id ='PRIORITY_LEVEL' AND c.description ='1-High' AND  b.party_id='"+loginPartyId+"'";
		
		rs = sqlProcessor.executeQuery(openSrSql);
		if (rs != null) {
			while (rs.next()) {
				openSrCount = rs.getLong("openSrCount");
			}
		}
		rs = sqlProcessor.executeQuery(inprogressSrSql);
		if (rs != null) {
			while (rs.next()) {
				inprogressSrCount = rs.getLong("inprogressSrCount");
			}
		}
		rs = sqlProcessor.executeQuery(pendingSrSql);
		if (rs != null) {
			while (rs.next()) {
				pendingSrCount = rs.getLong("pendingSrCount");
			}
		}
		rs = sqlProcessor.executeQuery(cancelledSrSql);
		if (rs != null) {
			while (rs.next()) {
				cancelledSrCount = rs.getLong("cancelledSrCount");
			}
		}
		/*rs = sqlProcessor.executeQuery(closedSrSql);
		if (rs != null) {
			while (rs.next()) {
				closedSrCount = rs.getLong("closedSrCount");
			}
		}*
		rs = sqlProcessor.executeQuery(atRiskSrSql);
		if (rs != null) {
			while (rs.next()) {
				atRiskSrCount = rs.getLong("atRiskCount");
			}
		}
		
		rs = sqlProcessor.executeQuery(overDueSrSql);
		if (rs != null) {
			while (rs.next()) {
				overDueSrCount = rs.getLong("overdueCount");
			}
		}
		
		rs = sqlProcessor.executeQuery(highPrioritySrSql);
		if (rs != null) {
			while (rs.next()) {
				highPrioritySrCount = rs.getLong("highPrioritySrCount");
			}
		}
		*/
	//}
	
	
	//SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
	String userLoginId1 = userLogin.get("userLoginId");
	Connection con = (Connection)sqlProcessor.getConnection();
	CallableStatement cstmt = null;
	//ResultSet rs = null;
	//srLocation = UtilValidate.isNotEmpty(srLocation) ? srLocation : "ALL";
	
	cstmt = con.prepareCall("{call kpi_user_level(?,?)}");
	
	cstmt.setString(1, userLoginId1);
	cstmt.setString(2, "ALL");
	rs = cstmt.executeQuery();
	
	if(rs !=null){
		try{
			int i = 0;
			while (rs.next()) {
				Map<String, Object> data = new HashMap<String, Object>();
				String elementcount = rs.getString(1);
				String  barType = rs.getString(2);
				
				if("Open".equals(barType)){
					openSrCount = Long.valueOf(elementcount);
				} else if("Open_not_scheduled".equals(barType)){
					inprogressSrCount = Long.valueOf(elementcount);
				} else if("completed_last_week".equals(barType)){
					overDueSrCount = Long.valueOf(elementcount);
				} else if("idle".equals(barType)){
					atRiskSrCount = Long.valueOf(elementcount);
				} else if("New".equals(barType)){
					pendingSrCount = Long.valueOf(elementcount);
				} else if("completed_last_month".equals(barType)){
					cancelledSrCount = Long.valueOf(elementcount);
				} else if("high_priority".equals(barType)){
					highPrioritySrCount = Long.valueOf(elementcount);
				}
				//println ("bar type ---> "+barType + " --->> "+elementcount)
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

dashboardBarContext.put("sr-open", openSrCount);
dashboardBarContext.put("sr-inprogress", inprogressSrCount);
dashboardBarContext.put("sr-pending", pendingSrCount);
dashboardBarContext.put("sr-cancelled", cancelledSrCount);
dashboardBarContext.put("sr-closed", closedSrCount);
dashboardBarContext.put("sr-at-risk", atRiskSrCount);
dashboardBarContext.put("sr-over-due", overDueSrCount);
dashboardBarContext.put("sr-high-priority", highPrioritySrCount);

//println ("dashboardBarContext----->"+dashboardBarContext)
context.put("dashboardBarContext", dashboardBarContext);


List<EntityCondition> conditions = new ArrayList<EntityCondition>();
conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
		EntityCondition.makeCondition("isDemoStore",EntityOperator.EQUALS,"N"),
		EntityCondition.makeCondition("isDemoStore",EntityOperator.EQUALS,null))
		);

String roleTypeId = DataUtil.getPartySecurityRole(delegator, userLoginPartyId)

List<String> csrRoles = new ArrayList<>();
String csrRolesList = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "CSR_ROLES", "CUST_SERVICE_REP");
if (UtilValidate.isNotEmpty(csrRolesList)) {
	if (UtilValidate.isNotEmpty(csrRolesList) && csrRolesList.contains(",")) {
		csrRoles = org.fio.admin.portal.util.DataUtil.stringToList(csrRolesList, ",");
	} else {
		csrRoles.add(csrRolesList);
	}	
}

boolean isFsr = false;
if(UtilValidate.isNotEmpty(csrRoles) && !csrRoles.contains(roleTypeId)) {
	Map<String, Object> buTeamData = org.fio.homeapps.util.DataUtil.getUserBuTeam(delegator, userLoginPartyId);
	String businessUnit = (String) buTeamData.get("businessUnit");
	if(UtilValidate.isNotEmpty(businessUnit)) {
		conditions.add(EntityCondition.makeCondition("primaryStoreGroupId", EntityOperator.EQUALS, businessUnit));
		EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
		println ("condition----1----->"+condition);
		List<GenericValue> locationList = EntityQuery.use(delegator).select("productStoreId","storeName").from("ProductStore").where(condition).queryList();
		context.put("locationList", UtilValidate.isNotEmpty(locationList) ? locationList : new ArrayList());
	}
	
} else {
	EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
	println ("condition----2----->"+condition);
	List<GenericValue> locationList = EntityQuery.use(delegator).select("productStoreId","storeName").from("ProductStore").where(condition).queryList();
	context.put("locationList", UtilValidate.isNotEmpty(locationList) ? locationList : new ArrayList());
	isFsr = true;
}
context.put("isFsr",isFsr);