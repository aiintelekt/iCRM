import java.util.function.Function
import java.util.stream.Collectors

import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.base.util.UtilMisc;

searchCriteria = request.getParameter("searchCriteria");
println('==searchCriteria==='+searchCriteria);
if(UtilValidate.isEmpty(searchCriteria)) {
	List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();
	Set<String> fieldsToSelect = new TreeSet<String>();
	fieldsToSelect.add("srTypeId");
	fieldsToSelect.add("srCategoryId");
	fieldsToSelect.add("srSubCategoryId");
	fieldsToSelect.add("srPriority");
	fieldsToSelect.add("status");
	fieldsToSelect.add("slaPeriodLvl");
	fieldsToSelect.add("srPeriodUnit");
	fieldsToSelect.add("slaPeriodLvl1");
	fieldsToSelect.add("slaPeriodLvl2");
	fieldsToSelect.add("slaPeriodLvl3");
	fieldsToSelect.add("slaEscPeriodHrsLvl1");
	fieldsToSelect.add("slaEscPeriodHrsLvl2");
	fieldsToSelect.add("slaEscPeriodHrsLvl3");
	fieldsToSelect.add("createdDate");
	fieldsToSelect.add("createdBy");
	fieldsToSelect.add("modifiedDate");
	fieldsToSelect.add("modifiedBy");
	List<GenericValue> srSlaSetups = EntityQuery.use(delegator).select(fieldsToSelect).from("SrSlaConfig").orderBy("-lastUpdatedTxStamp").queryList();
	if(srSlaSetups != null && srSlaSetups.size() > 0) {
		for(GenericValue slaSetup : srSlaSetups) {
			Map<String, Object> data = new HashMap<String, Object>();
			/*data.put("srTypeId",DataHelper.getCustRequestAssocDesc(delegator,CustRequestAssocConstants.SR_TYPE,slaSetup.getString("srTypeId")));
			data.put("srCategoryId",DataHelper.getCustRequestAssocDesc(delegator,CustRequestAssocConstants.SR_Category,slaSetup.getString("srCategoryId")));
			data.put("srSubCategoryId",DataHelper.getCustRequestAssocDesc(delegator,CustRequestAssocConstants.SR_SubCategory,slaSetup.getString("srSubCategoryId")));*/
			data.put("srTypeId",slaSetup.getString("srTypeId"));
			data.put("srCategoryId",slaSetup.getString("srCategoryId"));
			data.put("srSubCategoryId",slaSetup.getString("srSubCategoryId"));
			data.put("srPriority",slaSetup.getString("srPriority"));
			data.put("status",slaSetup.getString("status"));
			data.put("slaPeriodLvl",slaSetup.getString("slaPeriodLvl"));
			data.put("slaPeriodUnit",slaSetup.getString("srPeriodUnit"));
			data.put("slaPeriodLvl1",slaSetup.getString("slaPeriodLvl1"));
			data.put("slaPeriodLvl2",slaSetup.getString("slaPeriodLvl2"));
			data.put("slaPeriodLvl3",slaSetup.getString("slaPeriodLvl3"));
			data.put("slaEscPeriodHrsLvl1",slaSetup.getInteger("slaEscPeriodHrsLvl1"));
			data.put("slaEscPeriodHrsLvl2",slaSetup.getInteger("slaEscPeriodHrsLvl2"));
			data.put("slaEscPeriodHrsLvl3",slaSetup.getInteger("slaEscPeriodHrsLvl3"));
			data.put("createdDate",slaSetup.getString("createdDate"));
			data.put("createdBy",slaSetup.getString("createdBy"));
			data.put("modifiedDate",slaSetup.getString("modifiedDate"));
			data.put("modifiedBy",slaSetup.getString("modifiedBy"));
			results.add(data);
		}
	}
}else {
	context.put("searchCriteria", searchCriteria);
}

statusOptions = UtilMisc.toMap("ACTIVE", "Active", "IN_ACTIVE", "Inactive");
context.put("statusOptions", statusOptions);