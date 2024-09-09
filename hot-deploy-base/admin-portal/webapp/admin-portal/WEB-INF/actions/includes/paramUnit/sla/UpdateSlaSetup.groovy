import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;

import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import java.util.LinkedHashMap;
import org.fio.admin.portal.util.DataHelper;

delegator = request.getAttribute("delegator");

/*String srTypeId=request.getParameter("srTypeId");
String srCategoryId=request.getParameter("srCategoryId");
String srSubCategoryId=request.getParameter("srSubCategoryId");
String srPriority=request.getParameter("srPriority");*/

inputContext = new LinkedHashMap<String, Object>();

String slaConfigId=request.getParameter("slaConfigId");
if(UtilValidate.isNotEmpty(slaConfigId)) {
    GenericValue slaSetupConfig = EntityQuery.use(delegator).from("SrSlaConfig").where("slaConfigId",slaConfigId).queryOne();
	if(UtilValidate.isNotEmpty(slaSetupConfig)) {
	
		inputContext.put("slaConfigId", slaSetupConfig.get("slaConfigId"));
		inputContext.put("srTypeId", slaSetupConfig.get("srTypeId"));
		inputContext.put("srCategoryId", slaSetupConfig.get("srCategoryId"));
		inputContext.put("srSubCategoryId", slaSetupConfig.get("srSubCategoryId"));
		context.put("srSubCategoryId", slaSetupConfig.get("srSubCategoryId"));		
		inputContext.put("srPriority", slaSetupConfig.get("srPriority"));
		inputContext.put("status", slaSetupConfig.get("status"));
		context.put("isSlaReq", slaSetupConfig.get("isSlaRequired"));
		inputContext.put("slaSrResolution", slaSetupConfig.get("slaPeriodLvl"));
		inputContext.put("srResolutionUnit", slaSetupConfig.get("srPeriodUnit"));
		inputContext.put("slaEscalation1", slaSetupConfig.get("slaPeriodLvl1"));
		inputContext.put("escalationUnit1", slaSetupConfig.get("slaEscPeriodHrsLvl1"));
		inputContext.put("slaEscalation2", slaSetupConfig.get("slaPeriodLvl2"));
		inputContext.put("escalationUnit2", slaSetupConfig.get("slaEscPeriodHrsLvl2"));
		inputContext.put("slaEscalation3", slaSetupConfig.get("slaPeriodLvl3"));
		inputContext.put("escalationUnit3", slaSetupConfig.get("slaEscPeriodHrsLvl3"));
		inputContext.put("slaConfigId", slaSetupConfig.get("slaConfigId"));
		inputContext.put("isSlaRequired", slaSetupConfig.get("isSlaRequired"));
		inputContext.put("slaPreEscalation", slaSetupConfig.get("slaPreEscLvl"));
		inputContext.put("preEscalationUnit", slaSetupConfig.get("slaPreEscPeriod"));
	}
}

context.put("inputContext", inputContext);
