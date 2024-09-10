import java.sql.ResultSet;

import org.fio.admin.portal.util.DataUtil
import org.groupfio.common.portal.util.UtilCampaign
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
Map<String, Object> appBarContext = new HashMap<String, Object>();
ResultSet rs = null;
SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
Map<String, Object> dashboardBarContext = new LinkedHashMap<String, Object>();

long activeCustCount = 0;
long openOppoCount = 0;
long openActivityCount = 0;
long srCount = 0;
long orderCount = 0;
long noOfInvoices = 0;

long openCalls = 0;

if(UtilValidate.isNotEmpty(userLogin)) {
	String loginPartyId = userLogin.getString("partyId");
	String userLoginId = userLogin.getString("userLoginId");
	
	Debug.log("loginPartyId====="+loginPartyId);
	context.put("loginPartyId",loginPartyId);
	appBarContext.put("loginPartyId",loginPartyId);
	context.put("appBarContext", appBarContext);
	
	boolean hasFullAccess = org.fio.homeapps.util.DataUtil.hasFullPermission(delegator, userLoginId);
	
	String activeCountSql = "SELECT COUNT(*) as 'activeAcctCount' FROM `party` a INNER JOIN party_role b ON  a.party_id=b.party_id ";
	String active_where_con=" WHERE b.role_type_id='CUSTOMER' AND a.status_id='PARTY_ENABLED'";
	
	String openActivitySql = "SELECT COUNT(*) as 'openActivityCount' FROM `work_effort` a INNER JOIN `work_effort_party_assignment` b ON a.work_Effort_id=b.work_Effort_id WHERE a.current_status_id='IA_OPEN' AND b.`ROLE_TYPE_ID`='CUSTOMER'";
	String opn_act_where_con="";
	
	String srSql = "SELECT COUNT(*) as 'srCount' FROM cust_request a INNER JOIN party_role b ON a.from_party_id=b.party_id WHERE b.role_type_id='CUSTOMER' AND a.status_id IN('SR_OPEN','SR_IN_PROGRESS')";
	String sr_where_con="";
	
	String ordersSql = "SELECT COUNT(*) as 'orderCount' FROM rms_transaction_master rtm INNER JOIN party_role pr ON rtm.BILL_TO_PARTY_ID=pr.party_id AND ORDER_STATUS='ORDER_CREATED' AND pr.role_type_id='CUSTOMER'";
	String order_where_con="";
	
	if(!hasFullAccess) {	
		activeCountSql = activeCountSql +" INNER JOIN `party_relationship` c ON a.party_id=c.party_id_from ";
		active_where_con = active_where_con + " AND `PARTY_RELATIONSHIP_TYPE_ID`='RESPONSIBLE_FOR' AND `ROLE_TYPE_ID_FROM`='CUSTOMER' AND thru_date IS NULL AND (C.PARTY_ID_TO IN(SELECT `PARTY_ID` FROM `EMPL_POSITION_FULFILLMENT` WHERE `PARTY_ID_TO`='"+loginPartyId+"') OR C.PARTY_ID_TO='"+loginPartyId+"')" ;
		
		openActivitySql  = openActivitySql + " AND (a.PRIM_OWNER_ID IN (SELECT GET_PARTY_ID_USER_LOGIN(party_id) FROM `empl_position_fulfillment` WHERE `PARTY_ID_TO`='"+loginPartyId+"') OR a.PRIM_OWNER_ID= '"+userLoginId+"')";
		
		srSql = srSql + "  AND (RESPONSIBLE_PERSON IN (SELECT GET_PARTY_ID_USER_LOGIN(party_id) FROM `empl_position_fulfillment` WHERE `PARTY_ID_TO`='"+loginPartyId+"') OR RESPONSIBLE_PERSON= '"+userLoginId+"')";
		
		ordersSql = ordersSql + " AND (rtm.SALES_REPRESENTATIVE_ID IN (SELECT PARTY_ID_TO FROM `empl_position_fulfillment` WHERE `PARTY_ID_TO`=rtm.SALES_REPRESENTATIVE_ID AND `PARTY_ID_TO` = '"+loginPartyId+"' ) OR rtm.SALES_REPRESENTATIVE_ID = '"+loginPartyId+"')";
	}
	
	
	activeCountSql = activeCountSql + active_where_con;
	
	rs = sqlProcessor.executeQuery(activeCountSql);
	if (rs != null) {
		while (rs.next()) {
			activeCustCount = rs.getLong("activeAcctCount");
		}
	}
	
	rs = sqlProcessor.executeQuery(openActivitySql);
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
	
	
	String totalNoOfInvoiceSql = "SELECT COUNT(DISTINCT invoice_id) AS noOfInvoices FROM invoice_transaction_master";
	rs = sqlProcessor.executeQuery(totalNoOfInvoiceSql);
	if (rs != null) {
		while (rs.next()) {
			noOfInvoices = rs.getLong("noOfInvoices");
		}
	}
	
	
	String defalutMktCampaignId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DIRECT_CALL");
	
	String countQuery = "SELECT COUNT(*) AS rowcount FROM( SELECT CRM.MARKETING_CAMPAIGN_ID, CRM.CONTACT_LIST_ID, CRM.PARTY_ID FROM CALL_RECORD_MASTER CRM ";
	countQuery = countQuery  + " INNER JOIN MARKETING_CAMPAIGN MKTC ON CRM.MARKETING_CAMPAIGN_ID = MKTC.MARKETING_CAMPAIGN_ID AND MKTC.CAMPAIGN_TYPE_ID = 'PHONE_CALL' AND CRM.MARKETING_CAMPAIGN_ID <> '"+defalutMktCampaignId+"' AND MKTC.STATUS_ID = 'MKTG_CAMP_PUBLISHED'";
	countQuery = countQuery  + " WHERE ((MKTC.END_DATE IS NULL OR MKTC.END_DATE > NOW()) AND (MKTC.START_DATE IS NULL OR MKTC.START_DATE <= NOW())";
	countQuery = countQuery  + " AND (CRM.CALL_FINISHED = 'N' OR CRM.CALL_FINISHED IS NULL OR CRM.CALL_FINISHED = ''))";
	
	List<String> csrPartyList = UtilCampaign.getCsrList(delegator, userLogin, true);
	
	if (UtilValidate.isNotEmpty(csrPartyList)) {
		countQuery = countQuery + " AND CRM.CSR1_PARTY_ID IN ("+org.fio.admin.portal.util.DataUtil.toList(csrPartyList, "")+")";
	}	
	countQuery = countQuery + " GROUP BY CRM.CONTACT_LIST_ID, CRM.MARKETING_CAMPAIGN_ID,CRM.PARTY_ID) totalRecords";

	rs = sqlProcessor.executeQuery(countQuery);
	if (rs != null) {
		while (rs.next()) {
			openCalls = rs.getLong("rowcount");
		}
	}
	
	dashboardBarContext.put("customer-active", activeCustCount);
	//dashboardBarContext.put("customer-open-oppo", openOppoCount);
	dashboardBarContext.put("customer-open-act", openActivityCount);
	dashboardBarContext.put("customer-open-sr", srCount);
	dashboardBarContext.put("customer-open-order", orderCount);
	dashboardBarContext.put("no-of-invoices", noOfInvoices);
	dashboardBarContext.put("open-calls", openCalls);
	context.put("dashboardBarContext",dashboardBarContext);
}
