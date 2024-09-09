


import java.sql.Timestamp;

import org.fio.crm.party.PartyHelper;
import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.EnumUtil
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

import java.text.SimpleDateFormat;
import java.util.Locale
import java.util.TimeZone

import org.ofbiz.base.util.Debug;

String partyId = request.getParameter("partyId");
String rateTypeId = request.getParameter("rateTypeId");
String currencyUomId = request.getParameter("uomId");
String fromDate = request.getParameter("fromDate");

SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
SimpleDateFormat df1 = new SimpleDateFormat("MM/dd/yyyy");
fromDate = df.format(df1.parse(fromDate));

Timestamp fromDateTime = UtilDateTime.stringToTimeStamp(fromDate, "yyyy-MM-dd HH:mm:ss", TimeZone.getDefault(), Locale.getDefault());

inputContext = new LinkedHashMap<String, Object>();

if (UtilValidate.isNotEmpty(partyId)) {
	partyRate = from("PartyRate").where("partyId", partyId, "rateTypeId", rateTypeId, "currencyUomId", currencyUomId, "fromDate", fromDateTime).queryOne();
	if (UtilValidate.isNotEmpty(partyRate)) {
		String dafaultRate = partyRate.getString("defaultRate")
		inputContext.put("rate", partyRate.get("rate"));
		inputContext.put("currencyUomId", partyRate.get("currencyUomId"));
		inputContext.put("rateTypeId", partyRate.get("rateTypeId"));
		inputContext.put("currencyUomDesc",partyRate.get("currencyUomId"));
		inputContext.put("rateTypeDesc",org.fio.admin.portal.util.DataUtil.getRateType(delegator, rateTypeId));
		String technicianName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, partyRate.getString("partyId"), false);
		if(UtilValidate.isNotEmpty(technicianName)){
			inputContext.put("partyDesc", technicianName);
		}
		if(UtilValidate.isNotEmpty(dafaultRate) && "Y".equals(dafaultRate)){
			inputContext.put("partyDesc", "Standard Rates");
		}
		if(UtilValidate.isNotEmpty(partyRate.getTimestamp("fromDate"))){
			String dateStr = UtilDateTime.toDateString(partyRate.getTimestamp("fromDate"),"MM/dd/yyyy");
			//inputContext.put("fromDate",partyRate.getTimestamp("fromDate"));
			inputContext.put("fromDateDesc",dateStr);
			if (request.getRequestURI().contains("viewTechnicianRate")) {
				inputContext.put("fromDate",dateStr);
			}
			if (request.getRequestURI().contains("updateTechnicianRate")) {
				String fromDateStr = UtilDateTime.toDateString(partyRate.getTimestamp("fromDate"),"yyyy-MM-dd");
				inputContext.put("fromDate", fromDateStr);
			}
		}
		if(UtilValidate.isNotEmpty(partyRate.getTimestamp("thruDate"))){
			String dateStr = UtilDateTime.toDateString(partyRate.getTimestamp("thruDate"),"MM/dd/yyyy");
			//inputContext.put("thruDate",partyRate.getTimestamp("thruDate"));
			inputContext.put("thruDate",dateStr);
			if (request.getRequestURI().contains("viewTechnicianRate")) {
				inputContext.put("thruDate",dateStr);
			}
		}
	}
	context.put("partyId", partyId);
	context.put("inputContext", inputContext);
}


