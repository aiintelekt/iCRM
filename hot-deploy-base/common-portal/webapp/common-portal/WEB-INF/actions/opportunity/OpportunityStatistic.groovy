import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.fio.admin.portal.util.DataUtil
import org.fio.crm.party.PartyHelper;
import org.groupfio.account.portal.util.DataHelper;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.Debug;
import javolution.util.FastList;
import org.fio.homeapps.util.UtilActivity;
import org.fio.homeapps.util.EnumUtil;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("account-portalUiLabels", locale);

String partyId = request.getParameter("partyId");
String isView = context.get("isView");

ResultSet rs = null;
SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
String oppTypeEnabled = org.groupfio.common.portal.util.UtilOpportunity.isOpportunityTypeEnabled(delegator);
context.put("oppTypeEnabled", oppTypeEnabled);

Long totalOppCount=0;
Long totalWonOppCount=0;
Long totalLostOppCount=0;
Long totalOpenOppCount=0;
Long totalOppEstAmount=0;
Long OppWonPercent=0;
Long OppLossPercent=0;

Long totalLostAmount=0;
Long totalWonAmount =0;

String totalOppCountSql = "SELECT COUNT(*) as 'totalOppCount' FROM `sales_opportunity` WHERE  `party_id` ='"+partyId+"'";
rs = sqlProcessor.executeQuery(totalOppCountSql);
if (rs != null) {
	while (rs.next()) {
		totalOppCount = rs.getLong("totalOppCount");
	}
}
sqlProcessor.close();
String totalWonOppCountSql ="";
rs = null;
totalWonOppCountSql = "SELECT COUNT(*) as 'totalWonOppCount' FROM `sales_opportunity` WHERE  `party_id` ='"+partyId+"' AND OPPORTUNITY_STATUS_ID!='OPPO_VOID' AND `opportunity_stage_id` ='SOSTG_WON'  ";
rs = sqlProcessor.executeQuery(totalWonOppCountSql);
if (rs != null) {
	while (rs.next()) {
		totalWonOppCount = rs.getLong("totalWonOppCount");
	}
}
sqlProcessor.close();
String totalLostOppCountSql ="";
rs = null;
totalLostOppCountSql = "SELECT COUNT(*) as 'totalLostOppCount' FROM `sales_opportunity` WHERE  `party_id` ='"+partyId+"' AND OPPORTUNITY_STATUS_ID!='OPPO_VOID' AND `opportunity_stage_id` ='SOSTG_LOST'  ";
rs = sqlProcessor.executeQuery(totalLostOppCountSql);
if (rs != null) {
	while (rs.next()) {
		totalLostOppCount = rs.getLong("totalLostOppCount");
	}
}
sqlProcessor.close();

String totalOpenOppCountSql = "SELECT COUNT(*) as 'totalOpenOppCount' FROM `sales_opportunity` WHERE  `party_id` ='"+partyId+"' AND `opportunity_status_id` ='OPPO_OPEN'";
rs = sqlProcessor.executeQuery(totalOpenOppCountSql);
if (rs != null) {
	while (rs.next()) {
		totalOpenOppCount = rs.getLong("totalOpenOppCount");
	}
}
sqlProcessor.close();

String totalOppEstAmountSql = "SELECT SUM(estimated_amount) as 'totalOppEstAmount' FROM `sales_opportunity` WHERE  `party_id` ='"+partyId+"'";
rs = sqlProcessor.executeQuery(totalOppEstAmountSql);
if (rs != null) {
	while (rs.next()) {
		totalOppEstAmount = rs.getLong("totalOppEstAmount");
	}
}
sqlProcessor.close();


String totalOwnOppEstAmountSql = "SELECT SUM(estimated_amount) as 'totalWonOppEstAmount' FROM `sales_opportunity` WHERE  `party_id` ='"+partyId+"' AND OPPORTUNITY_STATUS_ID!='OPPO_VOID' AND `opportunity_stage_id` ='SOSTG_WON' ";
rs = sqlProcessor.executeQuery(totalOwnOppEstAmountSql);
if (rs != null) {
	while (rs.next()) {
		totalWonAmount = rs.getLong("totalWonOppEstAmount");
	}
}
sqlProcessor.close();

String totalLostOppEstAmountSql = "SELECT SUM(estimated_amount) as 'totalLostOppEstAmount' FROM `sales_opportunity` WHERE  `party_id` ='"+partyId+"' AND OPPORTUNITY_STATUS_ID!='OPPO_VOID' AND `opportunity_stage_id` ='SOSTG_LOST' ";
rs = sqlProcessor.executeQuery(totalLostOppEstAmountSql);
if (rs != null) {
	while (rs.next()) {
		totalLostAmount = rs.getLong("totalLostOppEstAmount");
	}
}
sqlProcessor.close();


double wonPerct = 0d;
double lostPerct = 0d;
String wonPerctStr = "0";
String lostPerctStr = "0";

if(totalWonOppCount>0) {
	//OppWonPercent = totalWonOppCount * 100 / totalOppCount;
	wonPerct = (totalWonOppCount / (totalWonOppCount+totalLostOppCount)) * 100;
	DecimalFormat myFormatter = new DecimalFormat("###.##");
	wonPerctStr = myFormatter.format(wonPerct);
}
if(totalLostOppCount>0) {
	//OppLossPercent = totalLostOppCount * 100 / totalOppCount;
	lostPerct = (totalLostOppCount / (totalWonOppCount+totalLostOppCount)) * 100;
	DecimalFormat myFormatter = new DecimalFormat("###.##");
	lostPerctStr = myFormatter.format(lostPerct);
}

	
context.put("totalOppCount", totalOppCount);
context.put("totalWonOppCount", totalWonOppCount);
context.put("totalLostOppCount", totalLostOppCount);
context.put("totalOpenOppCount", totalOpenOppCount);
context.put("totalOppEstAmount", totalOppEstAmount);
context.put("OppWonPercent", wonPerctStr);
context.put("OppLossPercent", lostPerctStr);

context.put("totalWonAmount", totalWonAmount);
context.put("totalLostAmount", totalLostAmount);
	