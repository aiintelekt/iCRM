import java.text.SimpleDateFormat
import java.util.function.Function
import java.util.stream.Collectors

import org.fio.admin.portal.constant.AdminPortalConstant.BusinessUnitConstant
import org.fio.admin.portal.constant.AdminPortalConstant.DateTimeTypeConstant
import org.fio.admin.portal.util.DataUtil
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil
import org.fio.admin.portal.util.DataHelper;
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;

List<GenericValue> teamDetails =  delegator.findByAnd("EmplTeam",  null, null, false);
context.put("teamId", DataHelper.getDropDownOptions(teamDetails, "emplTeamId", "teamName"));

List<GenericValue> parentBuDetails =  delegator.findByAnd("ProductStoreGroup", UtilMisc.toMap("status", BusinessUnitConstant.ACTIVE), null, false);
context.put("buId", DataHelper.getDropDownOptions(parentBuDetails, "productStoreGroupId", "productStoreGroupName"));

List<GenericValue> statusDetails = delegator.findByAnd("Enumeration",  UtilMisc.toMap("enumTypeId", BusinessUnitConstant.STATUS_ID), null, false);
context.put("statusId", DataHelper.getDropDownOptions(statusDetails, "enumCode", "description"));

inputContext = new LinkedHashMap<String, Object>();
context.put("inputContext", inputContext);

searchCriteria = request.getParameter("searchCriteria");
println('==searchCriteria==='+searchCriteria);
if(UtilValidate.isEmpty(searchCriteria)) {
	List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();
	Set<String> fieldsToSelect = new TreeSet<String>();
	fieldsToSelect.add("teamName");
	fieldsToSelect.add("businessUnit");
	fieldsToSelect.add("isActive");
	fieldsToSelect.add("emplTeamId");
	fieldsToSelect.add("createdOn");
    fieldsToSelect.add("createdBy");
    fieldsToSelect.add("modifiedOn");
    fieldsToSelect.add("modifiedBy");
	List<GenericValue> emplTeams = EntityQuery.use(delegator).select(fieldsToSelect).from("TeamBU").queryList();
	if(emplTeams != null && emplTeams.size() > 0)
	{
		for(GenericValue emplTeam : emplTeams) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("teamName", emplTeam.getString("emplTeamId"));
			data.put("emplTeamId", emplTeam.getString("emplTeamId"));
			if(UtilValidate.isNotEmpty(emplTeam.getString("businessUnit"))){
				GenericValue getBu = EntityQuery.use(delegator).from("ProductStoreGroup").where("productStoreGroupId", emplTeam.getString("businessUnit")).queryOne();
				if(UtilValidate.isNotEmpty(getBu)) {
				 data.put("buName", getBu.getString("productStoreGroupId"));
				}
			}
			if(UtilValidate.isNotEmpty(emplTeam.getString("isActive"))){
				GenericValue getStatus = EntityQuery.use(delegator).from("Enumeration").where("enumCode", emplTeam.getString("isActive"),"enumTypeId", BusinessUnitConstant.STATUS_ID).queryOne();
				if(UtilValidate.isNotEmpty(getStatus)) {
				 data.put("status", getStatus.getString("description"));
				}
			}
		 data.put("createdDate", emplTeam.getString("createdOn"));
         data.put("created", emplTeam.getString("createdBy"));
         data.put("modifiedDate", emplTeam.getString("modifiedOn"));
         data.put("modified", emplTeam.getString("modifiedBy"));
			results.add(data);
		}
	}
}
else {
	context.put("searchCriteria", searchCriteria);
}