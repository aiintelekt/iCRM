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

package org.groupfio.common.portal.event;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.util.SrDataHelper;
import org.fio.homeapps.util.UtilDateTime;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import javolution.util.FastMap;

/**
 * @author Sharif Ul Islam
 * 
 */
public final class ProgramTemplateEvents {

    private static final String MODULE = ProgramTemplateEvents.class.getName();
    
    public static String getProgramTemplateList(HttpServletRequest request, HttpServletResponse response) {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		//String emailEngine = request.getParameter("emailEngine");
		
		Map<String, Object> result = FastMap.newInstance();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		List<Map<String, Object>> dataList = new ArrayList<>();
		
		try {
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			
			DynamicViewEntity dynamicView = new DynamicViewEntity();
			
			dynamicView.addMemberEntity("CRA", "CustRequestAttribute");
			dynamicView.addAlias("CRA", "custRequestId", null, null, null, true, null);
			dynamicView.addAlias("CRA", "attrName");
			dynamicView.addAlias("CRA", "attrValue");
			
			dynamicView.addMemberEntity("CR", "CustRequest");
			dynamicView.addAlias("CR", "actualStartDate");
			dynamicView.addAlias("CR", "actualEndDate");
			dynamicView.addViewLink("CRA", "CR", Boolean.FALSE, ModelKeyMap.makeKeyMapList("custRequestId"));
			
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();
			conditions.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "IS_PROG_TPL"));
			conditions.add(EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, "Y"));
			conditions.add(EntityUtil.getFilterByDateExpr("actualStartDate", "actualEndDate"));
        	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			
        	List<GenericValue> attrList = EntityQuery.use(delegator).from(dynamicView).where(mainConditon).queryList();
			if (UtilValidate.isNotEmpty(attrList)) {
				Map<String, Object> custRequestNames = SrDataHelper.getCustRequestNames(delegator, attrList);
				
				for (GenericValue attr : attrList) {
					String srNumber = attr.getString("custRequestId");
					//GenericValue sr = EntityQuery.use(delegator).select("actualStartDate").from("CustRequest").where("custRequestId", srNumber).queryFirst();
					
					Map<String, Object> data = new LinkedHashMap<>();
					data.put("programTemplateId", attr.getString("custRequestId"));
					data.put("programTemplateName", custRequestNames.get(attr.getString("custRequestId")));
					
					data.put("daysRequired", org.groupfio.common.portal.util.SrUtil.getCustRequestAttrValue(delegator, "DAYS_REQUIRED", srNumber));
					data.put("displayFormat", org.groupfio.common.portal.util.SrUtil.getCustRequestAttrValue(delegator, "DISPLAY_FORMAT", srNumber));
					
					String fromDate = "";
					if (UtilValidate.isNotEmpty(attr.getTimestamp("actualStartDate"))) {
						fromDate = UtilDateTime.timeStampToString(attr.getTimestamp("actualStartDate"), globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
					}
					data.put("fromDate", fromDate);
					
					String thruDate = "";
					if (UtilValidate.isNotEmpty(attr.getTimestamp("actualEndDate"))) {
						thruDate = UtilDateTime.timeStampToString(attr.getTimestamp("actualEndDate"), globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
					}
					data.put("thruDate", thruDate);
					
					dataList.add(data);
				}
			}
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			result.put(ModelService.SUCCESS_MESSAGE, "Program Template Retrieved Successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		result.put("dataList", dataList);
		return AjaxEvents.doJSONResponse(response, result);
    }
    
}
