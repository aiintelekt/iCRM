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

inputContext = new LinkedHashMap<String, Object>();

String slaConfigId=request.getParameter("slaConfigId");
println("slaConfigId=="+slaConfigId);
if(UtilValidate.isNotEmpty(slaConfigId)) {
    GenericValue slaSetupConfig = EntityQuery.use(delegator).from("SrSlaConfig").where("slaConfigId",slaConfigId).queryOne();
	println("slaSetupConfig=="+slaSetupConfig);
	if(UtilValidate.isNotEmpty(slaSetupConfig)) {
		context.put("slaSetupConfig", slaSetupConfig);
		
		//inputContext.putAll(slaSetupConfig.getAllFields());
		
		inputContext.put("srTypeId", slaSetupConfig.srTypeId);
		String category =  org.fio.admin.portal.util.DataHelper.getCustRequestAssocDesc(delegator, "SRCategory" , slaSetupConfig.srCategoryId)
		inputContext.put("srCategoryId", UtilValidate.isNotEmpty(category) ? category : "NA");
		
		//inputContext.put("srTypeId", org.fio.admin.portal.util.DataHelper.getCustRequestAssocDesc(delegator, "SRTYPE" , slaSetupConfig.srTypeId));
		//inputContext.put("srCategoryId", org.fio.admin.portal.util.DataHelper.getCustRequestAssocDesc(delegator, "SRCategory" , slaSetupConfig.srCategoryId));
		String subCategory = org.fio.admin.portal.util.DataHelper.getCustRequestAssocDesc(delegator, "SRSubCategory" , slaSetupConfig.srSubCategoryId);
		inputContext.put("srSubCategoryId", UtilValidate.isNotEmpty(subCategory) ? subCategory : "NA");
		inputContext.put("srPriority", slaSetupConfig.srPriority);
		inputContext.put("srPriority_desc", org.fio.admin.portal.util.EnumUtil.getEnumDescription(delegator, "PRIORITY_LEVEL" , slaSetupConfig.srPriority));
		
		
		inputContext.put("status", slaSetupConfig.status);
		inputContext.put("slaSrResolution", slaSetupConfig.slaPeriodLvl);
		inputContext.put("srResolutionUnit", slaSetupConfig.srPeriodUnit);
		inputContext.put("slaEscalation1", slaSetupConfig.slaPeriodLvl1);
		inputContext.put("escalationUnit1", slaSetupConfig.slaEscPeriodHrsLvl1);
		inputContext.put("slaEscalation2", slaSetupConfig.slaPeriodLvl2);
		inputContext.put("escalationUnit2", slaSetupConfig.slaEscPeriodHrsLvl2);
		inputContext.put("slaEscalation3", slaSetupConfig.slaPeriodLvl3);
		inputContext.put("escalationUnit3", slaSetupConfig.slaEscPeriodHrsLvl3);
		inputContext.put("slaConfigId", slaSetupConfig.get("slaConfigId"));
		inputContext.put("isSlaRequired", slaSetupConfig.get("isSlaRequired"));
		inputContext.put("slaPreEscalation", slaSetupConfig.get("slaPreEscLvl"));
		inputContext.put("preEscalationUnit", slaSetupConfig.get("slaPreEscPeriod"));
	}
}

context.put("inputContext", inputContext);
