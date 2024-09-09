/*
 * Copyright (c) Open Source Strategies, Inc.
 * 
 * Opentaps is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Opentaps is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Opentaps.  If not, see <http://www.gnu.org/licenses/>.
 */

import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilDateTime;

import java.util.*;

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

import org.fio.crm.util.DataHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


partyId = request.getParameter("partyId");

partySummary = from("PartySummaryDetailsView").where("partyId", partyId,"partyTypeId","PARTY_GROUP").queryOne();

Map<String, Object> appBarContext = new HashMap<String, Object>();
if(partyId!=null){
	primaryContactInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,partyId,UtilMisc.toMap("isRetrivePhone", true, "isRetriveEmail", true),true);
	if(UtilValidate.isNotEmpty(primaryContactInformation)) {
		appBarContext.put("primaryEmail",primaryContactInformation.get("EmailAddress"));
		appBarContext.put("emailSolicitation",primaryContactInformation.get("emailSolicitation"));
		appBarContext.put("primaryPhone",primaryContactInformation.get("PrimaryPhone"));
		appBarContext.put("phoneSolicitation",primaryContactInformation.get("phoneSolicitation"));
	}
}

if(partySummary!=null && partySummary.size()>0){
	context.put("partySummary", partySummary);
	appBarContext.put("name", partySummary.get("groupName"));
}

context.put("appBarContext", appBarContext);

/*externalId = request.getParameter("srNumber");
security = request.getAttribute("security");
userLogin = request.getAttribute("userLogin");
HttpSession session = request.getSession();
userLoginId = userLogin.getAt('partyId');
if (UtilValidate.isNotEmpty(externalId)) {
	ResultSet rs = null;
	SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
	List < String > rmList = new ArrayList < String > ();
	if(UtilValidate.isNotEmpty(externalId)) {
		String sqlQureycond = "SELECT  *  FROM cust_request_sr_summary WHERE external_id ='" + externalId + "'";
		rs = sqlProcessor.executeQuery(sqlQureycond);
	}
	List responseCountList = new ArrayList();
	Map list = new HashMap();
	JSONObject responseCountJSONObj = new JSONObject();
	JSONArray responseObj = new JSONArray();
	custRequestGen = from('CustRequest').where('custRequestId', externalId).queryOne();
	String tatHours = "";
	String tatDays = "";
	if (UtilValidate.isNotEmpty(custRequestGen)) {
		if (UtilValidate.isNotEmpty(custRequestGen.getBigDecimal("tatHours"))) {
			tatHours = custRequestGen.getBigDecimal("tatHours").setScale(2,BigDecimal.ROUND_UP);
		}
		if (UtilValidate.isNotEmpty(custRequestGen.getBigDecimal("tatDays"))) {
			tatDays = custRequestGen.getBigDecimal("tatDays").setScale(0,BigDecimal.ROUND_UP);
		}
	}
	if (rs != null) {
		while (rs.next()) {
			JSONObject responseCountObj = new JSONObject();
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("custRequestId", rs.getString("cust_Request_Id"));
			data.put("externalId", rs.getString("external_Id"));
			data.put("customerName", rs.getString("customer_Name"));
			data.put("cinNumber", rs.getString("CIN_NUMBER"));
			data.put("prospectName", rs.getString("PROSPECT_NAME"));
			data.put("prospectId", rs.getString("prospect_Id"));
			data.put("isNonCrm", "No");
			if("Y".equals(rs.getString("is_Non_Crm")))
				data.put("isNonCrm", "Yes");
			data.put("vPlusId", rs.getString("v_Plus_Id"));
			data.put("nationalId", rs.getString("national_Id"));
			data.put("srTypeId", rs.getString("sr_Type_Id"));
			data.put("srTypeName", rs.getString("sr_Type_Name"));
			data.put("srCategoryName", rs.getString("sr_Category_Name")==null ? "" : rs.getString("sr_Category_Name").substring(0, Math.min(rs.getString("sr_Category_Name").length(), 8)));
			data.put("srSubCategoryName", rs.getString("sr_Sub_Category_Name")==null ? "" : rs.getString("sr_Sub_Category_Name").substring(0, Math.min(rs.getString("sr_Sub_Category_Name").length(), 8)));
			data.put("otherSrSubCategory", rs.getString("other_Sr_Sub_Category"));
			data.put("priority", rs.getString("priority"));
			data.put("srStatus", rs.getString("sr_Status_Id"));
			data.put("srSubStatus", rs.getString("sr_Sub_Status_Id"));
			data.put("description", rs.getString("description"));
			data.put("resolution", rs.getString("resolution"));
			if(UtilValidate.isNotEmpty(rs.getTimestamp("open_Date"))){
				String dateStr = UtilDateTime.toDateString(rs.getTimestamp("open_Date"),"dd/MM/yyyy hh:mm");
				data.put("openDate", dateStr);
			}
			if(UtilValidate.isNotEmpty(rs.getString("duration_Days")))
				data.put("durationDays", rs.getString("duration_Days").replace(".000000","")+" Days");
			if(UtilValidate.isNotEmpty(rs.getTimestamp("due_Date"))){
				String dateStr = UtilDateTime.toDateString(rs.getTimestamp("due_Date"),"dd/MM/yyyy hh:mm");
				data.put("dueDate", dateStr);
			}
			data.put("urgencyState", rs.getString("urgency_State"));
			data.put("overDueFlag", "No");
			if("Y".equals(rs.getString("over_Due_Flag")))
				data.put("overDueFlag", "Yes");
			data.put("ownerUserLoginId", rs.getString("owner_User_Login_Id"));
			data.put("ownerBuName", rs.getString("owner_Bu_Name"));
			data.put("businessUnit", rs.getString("owner_Bu"));
			data.put("linkedFrom", rs.getString("linked_From"));
			data.put("linkedTo", rs.getString("linked_To"));
			data.put("salesOpportunityId", rs.getString("sales_Opportunity_Id"));
			data.put("workEffortId", rs.getString("work_Effort_Id"));
			data.put("accountType", rs.getString("account_Type"));
			data.put("accountNumber", rs.getString("account_Number"));
			data.put("onceDone", rs.getString("once_Done"));
			data.put("empTeamId", rs.getString("emp_Team_Id"));
			data.put("slaRisk", rs.getString("sla_Risk"));
			data.put("slaFixed", rs.getString("sla_Fixed"));
			if(UtilValidate.isNotEmpty(rs.getTimestamp("created_On"))){
				String dateStr = UtilDateTime.toDateString(rs.getTimestamp("created_On"),"dd/MM/yyyy hh:mm");
				data.put("createdOn", dateStr);
			}
			data.put("createdByUserLoginId", rs.getString("created_By_User_Login_Id"));
			data.put("modifiedByUserLoginId", rs.getString("modified_By_User_Login_Id"));
			if(UtilValidate.isNotEmpty(rs.getTimestamp("modified_On"))){
				String dateStr = UtilDateTime.toDateString(rs.getTimestamp("modified_On"),"dd/MM/yyyy hh:mm");
				data.put("modifiedOn", dateStr);
			}
			if(UtilValidate.isNotEmpty(rs.getTimestamp("closed_On"))){
				String dateStr = UtilDateTime.toDateString(rs.getTimestamp("closed_On"),"dd/MM/yyyy hh:mm");
				data.put("closedOn", dateStr);
			}
			data.put("closedBy", rs.getString("closed_By"));
			data.put("tatHours",tatHours);
			data.put("tatDays",tatDays);
			context.put("responseObj", data);
		}
	}
}
EntityCondition searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
		EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLoginId));
emplPosFullfillDetails = EntityUtil.getFirst( delegator.findList("EmplPositionFulfillment", searchConditions,null, null, null, false) );
if(UtilValidate.isNotEmpty(emplPosFullfillDetails)&& UtilValidate.isNotEmpty(emplPosFullfillDetails.getAt("emplTeamId"))){
	context.putAt("emplTeamId", emplPosFullfillDetails.getAt("emplTeamId"));
}
if(UtilValidate.isNotEmpty(emplPosFullfillDetails)&& UtilValidate.isNotEmpty(emplPosFullfillDetails.getAt("businessUnit"))){
	context.putAt("businessUnit", emplPosFullfillDetails.getAt("businessUnit"));
}
searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
		EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, externalId));
salesChannelEnumId="";
partyRole="";
fromPartyId="";
custRequestDetails = EntityUtil.getFirst( delegator.findList("CustRequest", searchConditions,UtilMisc.toSet("salesChannelEnumId","custReqNatId","fromPartyId"), null, null, false));
if(UtilValidate.isNotEmpty(custRequestDetails)){
	if(UtilValidate.isNotEmpty(custRequestDetails.getAt("salesChannelEnumId")))
		salesChannelEnumId=custRequestDetails.getAt("salesChannelEnumId");
	else
		salesChannelEnumId="";
	if(UtilValidate.isNotEmpty(custRequestDetails.getAt("fromPartyId"))){
		fromPartyId=custRequestDetails.getAt("fromPartyId");
		partyRoleDetails = EntityUtil.getFirst( delegator.findList("PartyRole", EntityCondition.makeCondition(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, custRequestDetails.getAt("fromPartyId")),EntityOperator.AND,
				EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_EQUAL, "_NA_")),UtilMisc.toSet("roleTypeId","partyId"), null, null, false));
		partyRole=UtilValidate.isNotEmpty(partyRoleDetails)?partyRoleDetails.getAt("roleTypeId"):"";
	}
}
context.putAt("customerType", partyRole);
context.putAt("salesChannelEnumId", salesChannelEnumId);
custRequestWorkEffortDetails = EntityUtil.getFirst( delegator.findList("CustRequestWorkEffort", searchConditions,null, null, null, false) );
if(UtilValidate.isNotEmpty(custRequestWorkEffortDetails)&& UtilValidate.isNotEmpty(custRequestWorkEffortDetails.getAt("workEffortId"))){
	String workEffortId="";
	workEffortId=custRequestWorkEffortDetails.getAt("workEffortId");
	searchConditions = EntityCondition.makeCondition(EntityCondition.makeCondition("currentStatusId", EntityOperator.NOT_EQUAL, "IA_CLOSED"),EntityOperator.AND,
			EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
	WorkEffortDetails = EntityUtil.getFirst( delegator.findList("WorkEffort", searchConditions,UtilMisc.toSet("workEffortId"), null, null, false) );
	if(UtilValidate.isNotEmpty(WorkEffortDetails)){
		context.putAt("hasSrActivites", "Y");
		context.putAt("openActivities", WorkEffortDetails);
	}
	else{
		context.putAt("hasSrActivites", "N")
	}
}

operSrCount=0;
List < EntityCondition > conditions = new ArrayList < EntityCondition > ();
if(UtilValidate.isNotEmpty(fromPartyId)){
	conditions.add(EntityCondition.makeCondition("fromPartyId", EntityOperator.EQUALS, fromPartyId));
	conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SR_OPEN"));
	EntityCondition mainCondition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
	List < GenericValue > custRequest = EntityQuery.use(delegator).from("CustRequest").where(mainCondition).queryList();
	if(UtilValidate.isNotEmpty(custRequest)){
		operSrCount=custRequest.size();
	}
}
context.put("operSrCount", operSrCount);*/