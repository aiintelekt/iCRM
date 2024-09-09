import java.util.function.Function
import java.util.stream.Collectors

import org.fio.admin.portal.constant.AdminPortalConstant.ParamUnitConstant
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil

searchCriteria = request.getParameter("searchCriteria");
println('==searchCriteria==='+searchCriteria);
if(UtilValidate.isEmpty(searchCriteria)) {
	List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();
	
	Set < String > fieldsToSelect = new TreeSet < String > ();
            fieldsToSelect.add("enumId");
            fieldsToSelect.add("description");
            fieldsToSelect.add("sequenceId");
            
            List < GenericValue > parentActivitys = EntityQuery.use(delegator).select(fieldsToSelect).from("Enumeration").orderBy("-lastUpdatedTxStamp").queryList();
            // Debug.log("==parentActivitys=="+parentActivitys);
            if (parentActivitys != null && parentActivitys.size() > 0) {
                for (GenericValue parentActivity: parentActivitys) {
                    Map<String, Object> data = new HashMap<String, Object>();
                    data.put("enumId", parentActivity.getString("enumId"));
                    data.put("activityParentDesc", parentActivity.getString("description"));
                    
                    data.put("sequenceId", parentActivity.getString("sequenceId"));
                    String createdOn = "";
                    String createdBy = "";
                    String modifiedOn = "";
                    String modifiedBy = "";
                    if (UtilValidate.isNotEmpty(parentActivity.getString("enumId"))) {
                        GenericValue getParentActivity = EntityUtil.getFirst(delegator.findByAnd("WorkEffortAssocTriplet", UtilMisc.toMap("code", parentActivity.getString("enumId"), "type", ParamUnitConstant.RELATED_TO), null, false));
                        if (UtilValidate.isNotEmpty(getParentActivity)) {
                            createdOn = getParentActivity.getString("createdOn");
                            createdBy = getParentActivity.getString("createdBy");
                            modifiedOn = getParentActivity.getString("modifiedOn");
                            modifiedBy = getParentActivity.getString("modifiedBy");
                            String status = getParentActivity.getString("active");
                            String statusDesc = "";
                            GenericValue statusDetails = EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumCode", status, "enumTypeId", ParamUnitConstant.STATUS_ID), null, false));
                            if (statusDetails != null) {
                                statusDesc = statusDetails.getString("description");
                            }
                            data.put("statusDesc", statusDesc);
                            data.put("createdOn", createdOn);
                            data.put("createdBy", createdBy);
                            data.put("modifiedOn", modifiedOn);
                            data.put("modifiedBy", modifiedBy);
                            results.add(data);
                            //Debug.log("==results=="+results);
                        }
                    }

                }
            }
        }else {
	context.put("searchCriteria", searchCriteria);
}