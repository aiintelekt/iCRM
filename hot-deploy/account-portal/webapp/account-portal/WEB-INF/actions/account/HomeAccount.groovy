import java.sql.ResultSet;

import org.fio.homeapps.util.DataUtil
import org.ofbiz.base.util.*;
import org.ofbiz.entity.jdbc.SQLProcessor;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("account-portalUiLabels", locale);

exportTypeList = new LinkedHashMap<String, Object>();

exportTypeList.put("CSV", "CSV");
exportTypeList.put("EXCEL", "Excel");

context.put("exportTypeList", exportTypeList);

exportFileTypes = new LinkedHashMap<String, Object>();

exportFileTypes.put("IMPORT_FILE", "Import file");
exportFileTypes.put("ERROR_FILE", "Error file");

context.put("exportFileTypes", exportFileTypes);
/*
kpiMetric = org.groupfio.account.portal.util.DataHelper.prepareHomeKpiInfo(delegator, userLogin);
context.put("kpiMetric", kpiMetric);
*/
//account dashboard data
/*
 Map<String, Object> appBarContext = new HashMap<String, Object>();
 appBarContext = DataHelper.getAccountDashboardContext(delegator, userLogin);
 println ("appBarContext-------->"+appBarContext);
 context.put("appBarContext", appBarContext);
 */

ResultSet rs = null;
SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
Map<String, Object> dashboardBarContext = new LinkedHashMap<String, Object>();

long activeAccountCount = 0;
long openOppoCount = 0;
long openActivityCount = 0;
long srCount = 0;
long orderCount = 0;

if(UtilValidate.isNotEmpty(userLogin)) {
	String loginPartyId = userLogin.getString("partyId");
	String adminFullView_accHome = DataUtil.getGlobalValue(delegator, "ADMIN_FULL_VIEW_ACCHOME", "Y");
	
	if("admin".equals(loginPartyId) && !adminFullView_accHome.equalsIgnoreCase("N")) {
		//String superAdminAcctSql = "SELECT COUNT(*) as 'activeAcctCount' FROM `party` a INNER JOIN party_role b ON  a.party_id=b.party_id INNER JOIN `party_relationship` c ON a.party_id=c.party_id_from INNER JOIN user_login d ON c.party_id_to=d.party_id WHERE b.role_type_id='ACCOUNT' AND a.status_id='PARTY_ENABLED' AND `PARTY_RELATIONSHIP_TYPE_ID`='RESPONSIBLE_FOR' AND `ROLE_TYPE_ID_FROM`='ACCOUNT' AND thru_date IS NULL AND enabled='Y'";
	
		String superAdminOppoSql = "SELECT count(*) as 'openOppoCount' FROM `sales_opportunity` a INNER JOIN `sales_opportunity_role` b ON a.`SALES_OPPORTUNITY_ID`=b.`SALES_OPPORTUNITY_ID` WHERE a.`OPPORTUNITY_STATUS_ID`='OPPO_OPEN' AND b.`ROLE_TYPE_ID`='ACCOUNT'";
	
		String superAdminActivitySql = "SELECT COUNT(*) as 'openActivityCount' FROM `work_effort` a INNER JOIN `work_effort_party_assignment` b ON a.work_Effort_id=b.work_Effort_id WHERE a.current_status_id='IA_OPEN' AND b.`ROLE_TYPE_ID`='ACCOUNT'";
	
		String srSql = "SELECT COUNT(*) as 'srCount' FROM cust_request a INNER JOIN party_role b ON a.from_party_id=b.party_id WHERE b.role_type_id='ACCOUNT' AND a.status_id IN('SR_OPEN','SR_IN_PROGRESS')";
		
		String ordersSql = "SELECT COUNT(*) as 'orderCount' FROM order_header oh INNER JOIN party_role pr ON oh.bill_from_party_id=pr.party_id AND STATUS_ID='ORDER_CREATED' AND pr.role_type_id='ACCOUNT'";
		
		/*
		rs = sqlProcessor.executeQuery(superAdminAcctSql);
		if (rs != null) {
			while (rs.next()) {
				activeAccountCount = rs.getLong("activeAcctCount");
			}
		}
		*/
		
		rs = sqlProcessor.executeQuery(superAdminOppoSql);
		if (rs != null) {
			while (rs.next()) {
				openOppoCount = rs.getLong("openOppoCount");
			}
		}
		
		rs = sqlProcessor.executeQuery(superAdminActivitySql);
		if (rs != null) {
			while (rs.next()) {
				openActivityCount = rs.getLong("openActivityCount");
			}
		}
		
		rs = sqlProcessor.executeQuery(srSql);
		if (rs != null) {
			while (rs.next()) {
				srCount = rs.getLong("srCount");
			}
		}
	
		rs = sqlProcessor.executeQuery(ordersSql);
		if (rs != null) {
			while (rs.next()) {
				orderCount = rs.getLong("orderCount");
			}
		}
		
	} else {
		//String activeAcctSql = "SELECT COUNT(*) as 'activeAcctCount' FROM `PARTY` A INNER JOIN PARTY_ROLE B ON  A.PARTY_ID=B.PARTY_ID INNER JOIN `PARTY_RELATIONSHIP` C ON A.PARTY_ID=C.PARTY_ID_FROM INNER JOIN USER_LOGIN D ON C.PARTY_ID_TO=D.PARTY_ID WHERE B.ROLE_TYPE_ID='ACCOUNT' AND A.STATUS_ID='PARTY_ENABLED' AND `PARTY_RELATIONSHIP_TYPE_ID`='RESPONSIBLE_FOR' AND `ROLE_TYPE_ID_FROM`='ACCOUNT' AND THRU_DATE IS NULL AND ENABLED='Y' AND (C.PARTY_ID_TO IN(SELECT `PARTY_ID` FROM `EMPL_POSITION_FULFILLMENT` WHERE `PARTY_ID_TO`='"+loginPartyId+"') OR C.PARTY_ID_TO='"+loginPartyId+"')";
								
		String openOppoSql = "SELECT COUNT(*) as 'openOppoCount' FROM `sales_opportunity` a INNER JOIN `sales_opportunity_role` b ON a.`SALES_OPPORTUNITY_ID`=b.`SALES_OPPORTUNITY_ID` WHERE a.`OPPORTUNITY_STATUS_ID`='OPPO_OPEN' AND b.`ROLE_TYPE_ID`='ACCOUNT' AND (a.`OWNER_ID` IN (SELECT GET_PARTY_ID_USER_LOGIN(party_id) FROM `empl_position_fulfillment` WHERE `PARTY_ID_TO`='"+loginPartyId+"') OR a.OWNER_ID= GET_PARTY_ID_USER_LOGIN('"+loginPartyId+"'))";
	
		String activitiesSql = "SELECT COUNT(*) as 'openActivityCount' FROM `work_effort` a INNER JOIN `work_effort_party_assignment` b ON a.work_Effort_id=b.work_Effort_id WHERE a.current_status_id='IA_OPEN' AND b.`ROLE_TYPE_ID`='ACCOUNT' AND(a.PRIM_OWNER_ID IN (SELECT GET_PARTY_ID_USER_LOGIN(party_id) FROM `empl_position_fulfillment` WHERE `PARTY_ID_TO`='"+loginPartyId+"') OR a.PRIM_OWNER_ID= GET_PARTY_ID_USER_LOGIN('"+loginPartyId+"'))";
		
		String srSql = "SELECT COUNT(*) as 'srCount' FROM cust_request a INNER JOIN party_role b ON a.from_party_id=b.party_id WHERE b.role_type_id='ACCOUNT' AND a.status_id IN('SR_OPEN','SR_IN_PROGRESS') AND (RESPONSIBLE_PERSON IN (SELECT GET_PARTY_ID_USER_LOGIN(party_id) FROM `empl_position_fulfillment` WHERE `PARTY_ID_TO`='"+loginPartyId+"') OR RESPONSIBLE_PERSON= GET_PARTY_ID_USER_LOGIN('"+loginPartyId+"'))";
		
		String ordersSql = "SELECT COUNT(*) as 'orderCount' FROM order_header oh INNER JOIN party_role pr ON oh.bill_from_party_id=pr.party_id AND pr.role_type_id='ACCOUNT' AND STATUS_ID='ORDER_CREATED' AND (oh.CREATED_BY IN (SELECT GET_PARTY_ID_USER_LOGIN(party_id) FROM `empl_position_fulfillment` WHERE `PARTY_ID_TO`='"+loginPartyId+"') OR oh.CREATED_BY = GET_PARTY_ID_USER_LOGIN('"+loginPartyId+"'))";
		
		/*
		rs = sqlProcessor.executeQuery(activeAcctSql);
		if (rs != null) {
			while (rs.next()) {
				activeAccountCount = rs.getLong("activeAcctCount");
			}
		}
		*/
		rs = sqlProcessor.executeQuery(openOppoSql);
		if (rs != null) {
			while (rs.next()) {
				openOppoCount = rs.getLong("openOppoCount");
			}
		}
	
		rs = sqlProcessor.executeQuery(activitiesSql);
		if (rs != null) {
			while (rs.next()) {
				openActivityCount = rs.getLong("openActivityCount");
			}
		}
		
		rs = sqlProcessor.executeQuery(srSql);
		if (rs != null) {
			while (rs.next()) {
				srCount = rs.getLong("srCount");
			}
		}
	
		rs = sqlProcessor.executeQuery(ordersSql);
		if (rs != null) {
			while (rs.next()) {
				orderCount = rs.getLong("orderCount");
			}
		}
	}
	
	//dashboardBarContext.put("account-active", activeAccountCount);
	dashboardBarContext.put("account-open-oppo", openOppoCount);
	dashboardBarContext.put("account-open-act", openActivityCount);
	dashboardBarContext.put("account-open-sr", srCount);
	dashboardBarContext.put("account-open-order", orderCount);
	context.put("dashboardBarContext",dashboardBarContext);
}
