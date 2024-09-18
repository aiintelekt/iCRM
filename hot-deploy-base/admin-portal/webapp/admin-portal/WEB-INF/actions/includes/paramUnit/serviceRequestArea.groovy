import java.util.function.Function
import java.util.stream.Collectors

import org.fio.admin.portal.constant.AdminPortalConstant.AlertCategoryConstant
import org.fio.admin.portal.constant.AdminPortalConstant.ParamUnitConstant
import org.ofbiz.base.util.Debug
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
            fieldsToSelect.add("value");
            fieldsToSelect.add("code");
            fieldsToSelect.add("parentValue");
            fieldsToSelect.add("parentCode");
            fieldsToSelect.add("grandparentValue");
            fieldsToSelect.add("grandparentCode");
            fieldsToSelect.add("active");
            fieldsToSelect.add("sequenceNumber");
            fieldsToSelect.add("createdOn");
            fieldsToSelect.add("createdBy");
            fieldsToSelect.add("modifiedOn");
            fieldsToSelect.add("modifiedBy");
            String status = "";
            
            List < GenericValue > srCategories = EntityQuery.use(delegator).select(fieldsToSelect).from("CustRequestAssoc").orderBy("-lastUpdatedTxStamp").queryList();
            //Debug.log("==++++++++++=="+srCategories);
            if (srCategories != null && srCategories.size() > 0) {
                for (GenericValue srCategory: srCategories) {
                    Map < String, Object > data = new HashMap < String, Object > ();
                    data.put("parentCode", srCategory.getString("parentCode"));
					data.put("parentValue", srCategory.getString("parentValue"));
                    data.put("code", srCategory.getString("code"));
					data.put("value", srCategory.getString("value"));
                    data.put("seqNo", srCategory.getString("sequenceNumber"));
                    status = srCategory.getString("active");
                    
                    if(UtilValidate.isNotEmpty(status)){
                        GenericValue getStatus = EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumCode", status,"enumTypeId",AlertCategoryConstant.STATUS_ID), null, false));
                        if (UtilValidate.isNotEmpty(getStatus)){
                            data.put("status",getStatus.getString("description"));
                        }
                    }
            		
            		data.put("createdOn", srCategory.getString("createdOn"));
                    data.put("createdBy", srCategory.getString("createdBy"));
                    data.put("modifiedOn", srCategory.getString("modifiedOn"));
                    data.put("modifiedBy",srCategory.getString("modifiedBy"));
                    results.add(data);
                        }
                    }
                    
                }
            
else {
	context.put("searchCriteria", searchCriteria);
}