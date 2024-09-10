


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

String workEffortPurposeTypeId = request.getParameter("workEffortPurposeTypeId");

inputContext = new LinkedHashMap<String, Object>();

if (UtilValidate.isNotEmpty(partyId)) {
	workEffortPurposeType = from("WorkEffortPurposeType").where("workEffortPurposeTypeId", workEffortPurposeTypeId).queryFirst();
	if (UtilValidate.isNotEmpty(workEffortPurposeType)) {
		inputContext.put("workEffortPurposeTypeId", workEffortPurposeType.get("workEffortPurposeTypeId"));
		inputContext.put("description", workEffortPurposeType.get("description"));
	}
	context.put("workEffortPurposeTypeId", workEffortPurposeTypeId);
	context.put("inputContext", inputContext);
}


