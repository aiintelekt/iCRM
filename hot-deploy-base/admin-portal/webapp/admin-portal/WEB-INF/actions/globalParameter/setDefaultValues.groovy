import javax.swing.DebugGraphics
import org.fio.admin.portal.constant.AdminPortalConstant;
import org.fio.admin.portal.util.DataHelper
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.base.util.*
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

inputContext = new LinkedHashMap<String, Object>();

List<List<GenericValue>> sectionSeperated =  new ArrayList<List<GenericValue>>();
List<GenericValue> listSectionDetails = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "DEFAULT_VALUES"), null, false);
sectionDetails = EntityUtil.getFirst(listSectionDetails);

if (UtilValidate.isNotEmpty(sectionDetails)){

String enumId = sectionDetails.getString("enumId");

GenericValue defaultRMUserGv = EntityUtil.getFirst(delegator.findByAnd("PretailLoyaltyGlobalParameters",
	UtilMisc.toMap("parameterId", "DEFAULT_RM_USER"), null, false));
 if (UtilValidate.isNotEmpty(defaultRMUserGv)){
	 
	 inputContext.put("ownerPartyId", defaultRMUserGv.getString("value"));
 }
 GenericValue defaultCountryGv = EntityUtil.getFirst(delegator.findByAnd("PretailLoyaltyGlobalParameters",
	UtilMisc.toMap("parameterId", "DEFAULT_COUNTRY"), null, false));
 if (UtilValidate.isNotEmpty(defaultCountryGv)){
	 
	 inputContext.put("generalCountryGeoId", defaultCountryGv.getString("value"));
 }
 GenericValue defaultUomGv = EntityUtil.getFirst(delegator.findByAnd("PretailLoyaltyGlobalParameters",
	UtilMisc.toMap("parameterId", "DEFAULT_CURRENCY_UOM"), null, false));
 if (UtilValidate.isNotEmpty(defaultUomGv)){
	 
	 inputContext.put("currencyUomId", defaultUomGv.getString("value"));
 }
 GenericValue defaultSrSlaGv = EntityUtil.getFirst(delegator.findByAnd("PretailLoyaltyGlobalParameters",
	 UtilMisc.toMap("parameterId", "DEFAULT_SR_SLA"), null, false));
 if (UtilValidate.isNotEmpty(defaultSrSlaGv)){
	  
	  inputContext.put("defaultSrSla", defaultSrSlaGv.getString("value"));
	  inputContext.put("defaultSrSlaUnit", defaultSrSlaGv.getString("description"));
	
	  
  }
inputContext.put("enumId", enumId);


List<GenericValue> parameterDetails = delegator.findList("PretailLoyaltyGlobalParameters", EntityCondition.makeCondition("parameterId", EntityOperator.IN, UtilMisc.toList("WORK_START_TIME", "WORK_END_TIME")), null, null, null, false);
if(UtilValidate.isNotEmpty(parameterDetails)){
workStartTimeGv = EntityUtil.getFirst(EntityUtil.filterByCondition(parameterDetails, EntityCondition.makeCondition("parameterId",EntityOperator.EQUALS,"WORK_START_TIME")));
if(UtilValidate.isNotEmpty(workStartTimeGv)){
workStartTime = workStartTimeGv.value

context.put("workStartTime", workStartTime);
inputContext.put("workStartTime", workStartTime);

}

workEndTimeGv = EntityUtil.getFirst(EntityUtil.filterByCondition(parameterDetails, EntityCondition.makeCondition("parameterId",EntityOperator.EQUALS,"WORK_END_TIME")));
if(UtilValidate.isNotEmpty(workEndTimeGv)){
	workEndTime = workEndTimeGv.value
	
	context.put("workEndTime", workEndTime);
	inputContext.put("workEndTime", workEndTime);
		
}

}

}

context.put("inputContext", inputContext);
