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

import org.fio.campaign.common.UtilCommon;
import org.fio.campaign.util.StringUtil;

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

import org.fio.campaign.util.CampaignUtil;
import org.fio.crm.util.DataHelper;
import org.fio.campaign.util.LoginFilterUtil;
import org.fio.homeapps.util.ResponseUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.fio.campaign.events.AjaxEvents;

String requestUri=request.getRequestURI();


EntityCondition searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
		EntityCondition.makeCondition("requestUri", EntityOperator.EQUALS, requestUri));


OfbizPageSecurityDetails = EntityUtil.getFirst( delegator.findList("OfbizPageSecurity", searchConditions,null, null, null, false) );

userLogin = request.getAttribute("userLogin");
String userLoginId = userLogin.get("partyId");

if (UtilValidate.isEmpty(OfbizPageSecurityDetails)) {
	EntityCondition searchConditions1 = EntityCondition.makeCondition(EntityOperator.AND,
			EntityCondition.makeCondition("requestUri", EntityOperator.EQUALS, requestUri));


	OfbizPageSecurityDetails = EntityUtil.getFirst( delegator.findList("OfbizTabSecurityShortcut", searchConditions1,null, null, null, false) );
}


EntityCondition partyRoleCondition = EntityCondition.makeCondition(EntityOperator.OR,
		EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumTypeId")), EntityOperator.EQUALS, "SCROLL_MSG"),
		);
PartyRoleDetailsl = delegator.findList("Enumeration", partyRoleCondition, null, null, null, false);

//if (UtilValidate.isNotEmpty(PartyRoleDetailsl)) {
roleTypeIds = EntityUtil.getFieldListFromEntityList(PartyRoleDetailsl, "enumId", true);
if (UtilValidate.isNotEmpty(OfbizPageSecurityDetails)) {

	String componentId=OfbizPageSecurityDetails.get("componentId");


	/*EntityCondition searchCond = EntityCondition.makeCondition(EntityOperator.AND,
	 EntityCondition.makeCondition("componentId", EntityOperator.EQUALS, componentId),
	 EntityUtil.getFilterByDateExpr());*/
	Timestamp today=UtilDateTime.nowTimestamp();
	Timestamp dayEnd = UtilDateTime.getDayEnd(today);
	Timestamp dayBegin = UtilDateTime.getDayStart(today);


	List<EntityCondition> conditionlist = FastList.newInstance();
	conditionlist.add(EntityCondition.makeCondition("componentId", EntityOperator.EQUALS, componentId));
	conditionlist.add(EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, "Y"));
	conditionlist.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, dayBegin));

	conditionlist.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayEnd)));
	conditionlist.add(EntityCondition.makeCondition(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLoginId), EntityOperator.OR,EntityCondition.makeCondition(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "ALL"))));
	if (UtilValidate.isNotEmpty(roleTypeIds)) {
		conditionlist
				.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roleTypeIds));
	}

	EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);

	//Debug.log("condition---------"+condition);
	GlobalMessageConfigDetails =  delegator.findList("GlobalMessageConfig", condition,null, null, null, false);
	//Debug.log("GlobalMessageConfigDetails---------"+GlobalMessageConfigDetails);
	context.GlobalMessageConfigDetails=GlobalMessageConfigDetails;
	if (UtilValidate.isNotEmpty(GlobalMessageConfigDetails)) {

		String description="";
		for(int i=0;i<GlobalMessageConfigDetails.size();i++){
			description+=GlobalMessageConfigDetails[i].getString("description")+'      '
		}
		context.globalDescription=description;
	}
}

//}


