import java.util.function.Function
import java.util.stream.Collectors

import org.fio.admin.portal.constant.AdminPortalConstant.ParamUnitConstant
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil

searchCriteria = request.getParameter("searchCriteria");
println('==searchCriteria==='+searchCriteria);
if(UtilValidate.isEmpty(searchCriteria)) {
	List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();
	
	Set < String > fieldsToSelect = new TreeSet < String > ();
            fieldsToSelect.add("code");
            fieldsToSelect.add("value");
            fieldsToSelect.add("parentCode");
            fieldsToSelect.add("parentValue");
            fieldsToSelect.add("active");
            fieldsToSelect.add("sequenceNumber");
            fieldsToSelect.add("createdOn");
            fieldsToSelect.add("createdBy");
            fieldsToSelect.add("modifiedOn");
            fieldsToSelect.add("modifiedBy");
            List < GenericValue > parentActivitys = EntityQuery.use(delegator).select(fieldsToSelect).from("WorkEffortAssocTriplet").orderBy("-lastUpdatedTxStamp").queryList();
           // Debug.log("==parentActivitys=="+parentActivitys);
            if (parentActivitys != null && parentActivitys.size() > 0) {
                for (GenericValue parentActivity: parentActivitys) {
                    Map < String, Object > data = new HashMap < String, Object > ();
                    data.put("enumId", parentActivity.getString("code"));
                    data.put("parentEnumId", parentActivity.getString("parentCode"));
                    data.put("activityTypeDesc", parentActivity.getString("value"));
                    String status = parentActivity.getString("active");
                    String statusDesc ="";
                    GenericValue statusDetails = EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumCode", status,"enumTypeId",ParamUnitConstant.STATUS_ID), null, false));
            		if (statusDetails != null) {
            			statusDesc = statusDetails.getString("description");
            		}
            		data.put("statusDesc", statusDesc);
                    data.put("sequenceId", parentActivity.getString("sequenceNumber"));
                    
                            data.put("parentValue",parentActivity.getString("parentValue"));
                            data.put("createdOn", parentActivity.getString("createdOn"));
                            data.put("createdBy", parentActivity.getString("createdBy"));
                            data.put("modifiedOn", parentActivity.getString("modifiedOn"));
                            data.put("modifiedBy", parentActivity.getString("modifiedBy"));
                            results.add(data);
                        }
                    }
                    
                    //Debug.log("==results=="+results);
                }else {
	context.put("searchCriteria", searchCriteria);
}