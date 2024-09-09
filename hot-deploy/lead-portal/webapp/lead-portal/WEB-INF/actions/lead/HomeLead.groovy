import java.sql.ResultSet;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.jdbc.SQLProcessor;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("lead-portalUiLabels", locale);

exportTypeList = new LinkedHashMap<String, Object>();

exportTypeList.put("CSV", "CSV");
exportTypeList.put("EXCEL", "Excel");

context.put("exportTypeList", exportTypeList);

exportFileTypes = new LinkedHashMap<String, Object>();

exportFileTypes.put("IMPORT_FILE", "Import file");
exportFileTypes.put("ERROR_FILE", "Error file");

context.put("exportFileTypes", exportFileTypes);
/*
kpiMetric = org.groupfio.lead.portal.util.DataHelper.prepareHomeKpiInfo(delegator, userLogin);
context.put("kpiMetric", kpiMetric);
*/
/*
 //lead dashboard data
 Map<String, Object> dashboardBarContext = new HashMap<String, Object>();
 dashboardBarContext = DataHelper.getLeadDashboardContext(delegator, userLogin);
 context.put("dashboardBarContext", dashboardBarContext);
 */


ResultSet rs = null;
SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
Map<String, Object> dashboardBarContext = new LinkedHashMap<String, Object>();

long universeCount = 0;
long suspectCount = 0;
long prospectCount = 0;
long targetCount = 0;
long qualifiedCount = 0;
long onholdCount = 0;
long disqualifiedCount = 0;
if(UtilValidate.isNotEmpty(userLogin)) {
	/*
	String loginPartyId = userLogin.getString("partyId");
	
	if("admin".equals(loginPartyId)) {
		String superAdminUniverseSql = "SELECT COUNT(*) as 'universeCount' FROM party WHERE role_type_id='LEAD' AND status_id='LEAD_UNIVERSE'";
		String superAdminSuspectSql = "SELECT COUNT(*)  as 'suspectCount' FROM party WHERE role_type_id='LEAD' AND status_id='LEAD_SUSPECT'";
		String superAdminProspectSql = "SELECT COUNT(*) as 'prospectCount' FROM party WHERE role_type_id='LEAD' AND status_id='LEAD_PROSPECT'";
		String superAdminTargetSql = "SELECT COUNT(*) as 'targetCount' FROM party WHERE role_type_id='LEAD' AND status_id='LEAD_TARGET'";
		String superAdminQualifiedSql = "SELECT COUNT(*) as 'qualifiedCount' FROM party WHERE role_type_id='LEAD' AND status_id='LEAD_QUALIFIED'";
		
		String superAdminHoldfiedSql = "SELECT COUNT(*) as 'onholdCount' FROM party WHERE role_type_id='LEAD' AND status_id='LEAD_ON_HOLD'";
		String superAdminDisQualifiedSql = "SELECT COUNT(*) as 'disqualifiedCount' FROM party WHERE role_type_id='LEAD' AND status_id='LEAD_DISQUALIFIED'";
		
		rs = sqlProcessor.executeQuery(superAdminUniverseSql);
		if (rs != null) {
			while (rs.next()) {
				universeCount = rs.getLong("universeCount");
			}
		}
		
		rs = sqlProcessor.executeQuery(superAdminSuspectSql);
		if (rs != null) {
			while (rs.next()) {
				suspectCount = rs.getLong("suspectCount");
			}
		}
			
		rs = sqlProcessor.executeQuery(superAdminProspectSql);
		if (rs != null) {
			while (rs.next()) {
				prospectCount = rs.getLong("prospectCount");
			}
		}
		
		rs = sqlProcessor.executeQuery(superAdminTargetSql);
		if (rs != null) {
			while (rs.next()) {
				targetCount = rs.getLong("targetCount");
			}
		}
			
		rs = sqlProcessor.executeQuery(superAdminQualifiedSql);
		if (rs != null) {
			while (rs.next()) {
				qualifiedCount = rs.getLong("qualifiedCount");
			}
		}
		rs = sqlProcessor.executeQuery(superAdminHoldfiedSql);
		if (rs != null) {
			while (rs.next()) {
				onholdCount = rs.getLong("onholdCount");
			}
		}
		rs = sqlProcessor.executeQuery(superAdminDisQualifiedSql);
		if (rs != null) {
			while (rs.next()) {
				disqualifiedCount = rs.getLong("disqualifiedCount");
			}
		}
		
	} else {
		String universeSql = "SELECT COUNT(*) as 'universeCount' FROM `party` a INNER JOIN party_role b ON  a.party_id=b.party_id INNER JOIN `party_relationship` c ON a.party_id=c.party_id_from INNER JOIN user_login d ON c.party_id_to=d.party_id WHERE b.role_type_id='LEAD' AND a.status_id='LEAD_UNIVERSE' AND `PARTY_RELATIONSHIP_TYPE_ID`='RESPONSIBLE_FOR' AND `ROLE_TYPE_ID_FROM`='LEAD' AND thru_date IS NULL AND enabled='Y' AND (c.party_id_to IN(SELECT `PARTY_ID` FROM `empl_position_fulfillment` WHERE `PARTY_ID_TO`='"+loginPartyId+"') OR c.party_id_to='"+loginPartyId+"')";
	
		String suspectSql = "SELECT COUNT(*) as 'suspectCount' FROM `party` a INNER JOIN party_role b ON  a.party_id=b.party_id INNER JOIN `party_relationship` c ON a.party_id=c.party_id_from INNER JOIN user_login d ON c.party_id_to=d.party_id WHERE b.role_type_id='LEAD' AND a.status_id='LEAD_SUSPECT' AND `PARTY_RELATIONSHIP_TYPE_ID`='RESPONSIBLE_FOR' AND `ROLE_TYPE_ID_FROM`='LEAD' AND thru_date IS NULL AND enabled='Y' AND (c.party_id_to IN(SELECT `PARTY_ID` FROM `empl_position_fulfillment` WHERE `PARTY_ID_TO`='"+loginPartyId+"')  OR c.party_id_to='"+loginPartyId+"')";
	
		String prospectSql = "SELECT COUNT(*) as 'prospectCount' FROM `party` a INNER JOIN party_role b ON  a.party_id=b.party_id INNER JOIN `party_relationship` c ON a.party_id=c.party_id_from INNER JOIN user_login d ON c.party_id_to=d.party_id WHERE b.role_type_id='LEAD' AND a.status_id='LEAD_PROSPECT' AND `PARTY_RELATIONSHIP_TYPE_ID`='RESPONSIBLE_FOR' AND `ROLE_TYPE_ID_FROM`='LEAD' AND thru_date IS NULL AND enabled='Y' AND (c.party_id_to IN(SELECT `PARTY_ID` FROM `empl_position_fulfillment` WHERE `PARTY_ID_TO`='"+loginPartyId+"') OR c.party_id_to='"+loginPartyId+"')";
	
		String targetSql = "SELECT COUNT(*) as 'targetCount' FROM `party` a INNER JOIN party_role b ON  a.party_id=b.party_id INNER JOIN `party_relationship` c ON a.party_id=c.party_id_from INNER JOIN user_login d ON c.party_id_to=d.party_id WHERE b.role_type_id='LEAD' AND a.status_id='LEAD_TARGET' AND `PARTY_RELATIONSHIP_TYPE_ID`='RESPONSIBLE_FOR' AND `ROLE_TYPE_ID_FROM`='LEAD' AND thru_date IS NULL AND enabled='Y' AND (c.party_id_to IN(SELECT `PARTY_ID` FROM `empl_position_fulfillment` WHERE `PARTY_ID_TO`='"+loginPartyId+"') OR c.party_id_to='"+loginPartyId+"')";
	
		String qualifiedSql = "SELECT COUNT(*) as 'qualifiedCount' FROM `party` a INNER JOIN party_role b ON  a.party_id=b.party_id INNER JOIN `party_relationship` c ON a.party_id=c.party_id_from INNER JOIN user_login d ON c.party_id_to=d.party_id WHERE b.role_type_id='LEAD' AND a.status_id='LEAD_QUALIFIED' AND `PARTY_RELATIONSHIP_TYPE_ID`='RESPONSIBLE_FOR' AND `ROLE_TYPE_ID_FROM`='LEAD' AND thru_date IS NULL AND enabled='Y' AND (c.party_id_to IN(SELECT `PARTY_ID` FROM `empl_position_fulfillment` WHERE `PARTY_ID_TO`='"+loginPartyId+"') OR c.party_id_to='"+loginPartyId+"')";
	
		String holdfiedSql = "SELECT COUNT(*) as 'onholdCount' FROM `party` a INNER JOIN party_role b ON  a.party_id=b.party_id INNER JOIN `party_relationship` c ON a.party_id=c.party_id_from INNER JOIN user_login d ON c.party_id_to=d.party_id WHERE b.role_type_id='LEAD' AND a.status_id='LEAD_ON_HOLD' AND `PARTY_RELATIONSHIP_TYPE_ID`='RESPONSIBLE_FOR' AND `ROLE_TYPE_ID_FROM`='LEAD' AND thru_date IS NULL AND enabled='Y' AND (c.party_id_to IN(SELECT `PARTY_ID` FROM `empl_position_fulfillment` WHERE `PARTY_ID_TO`='"+loginPartyId+"') OR c.party_id_to='"+loginPartyId+"')";
		
		String disqualifiedSql = "SELECT COUNT(*) as 'disqualifiedCount' FROM `party` a INNER JOIN party_role b ON  a.party_id=b.party_id INNER JOIN `party_relationship` c ON a.party_id=c.party_id_from INNER JOIN user_login d ON c.party_id_to=d.party_id WHERE b.role_type_id='LEAD' AND a.status_id='LEAD_DISQUALIFIED' AND `PARTY_RELATIONSHIP_TYPE_ID`='RESPONSIBLE_FOR' AND `ROLE_TYPE_ID_FROM`='LEAD' AND thru_date IS NULL AND enabled='Y' AND (c.party_id_to IN(SELECT `PARTY_ID` FROM `empl_position_fulfillment` WHERE `PARTY_ID_TO`='"+loginPartyId+"') OR c.party_id_to='"+loginPartyId+"')";
		
		rs = sqlProcessor.executeQuery(universeSql);
		if (rs != null) {
			while (rs.next()) {
				universeCount = rs.getLong("universeCount");
			}
		}
		
		rs = sqlProcessor.executeQuery(suspectSql);
		if (rs != null) {
			while (rs.next()) {
				suspectCount = rs.getLong("suspectCount");
			}
		}
		
		rs = sqlProcessor.executeQuery(prospectSql);
		if (rs != null) {
			while (rs.next()) {
				prospectCount = rs.getLong("prospectCount");
			}
		}
	
		rs = sqlProcessor.executeQuery(targetSql);
		if (rs != null) {
			while (rs.next()) {
				targetCount = rs.getLong("targetCount");
			}
		}
	
		rs = sqlProcessor.executeQuery(qualifiedSql);
		if (rs != null) {
			while (rs.next()) {
				qualifiedCount = rs.getLong("qualifiedCount");
			}
		}
		
		rs = sqlProcessor.executeQuery(holdfiedSql);
		if (rs != null) {
			while (rs.next()) {
				onholdCount = rs.getLong("onholdCount");
			}
		}
	
		rs = sqlProcessor.executeQuery(disqualifiedSql);
		if (rs != null) {
			while (rs.next()) {
				disqualifiedCount = rs.getLong("disqualifiedCount");
			}
		}
	
	}
	
	dashboardBarContext.put("lead_universe", universeCount);
	dashboardBarContext.put("lead_suspect", suspectCount);
	dashboardBarContext.put("lead_prospect", prospectCount);
	dashboardBarContext.put("lead_target", targetCount);
	dashboardBarContext.put("lead_qualified", qualifiedCount);
	dashboardBarContext.put("lead_on_hold", onholdCount);
	dashboardBarContext.put("lead_disqualified", disqualifiedCount);
	context.put("dashboardBarContext", dashboardBarContext);
	*/
}



