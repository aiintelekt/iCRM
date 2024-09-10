import java.util.function.Function
import java.util.stream.Collectors

import org.fio.admin.portal.constant.AdminPortalConstant.AlertCategoryConstant
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
            List < GenericValue > srSubCategories = EntityQuery.use(delegator).select(fieldsToSelect).from("CustRequestAssoc").orderBy("-lastUpdatedTxStamp").queryList();
            //Debug.log("==*************=="+srSubAreas);
            if (srSubCategories != null && srSubCategories.size() > 0) {
                for (GenericValue srSubCategory: srSubCategories) {
                    Map < String, Object > data = new HashMap < String, Object > ();
                    data.put("custRequestCategoryId", srSubCategory.getString("code"));
					data.put("value", srSubCategory.getString("value"));
                    data.put("parentCustRequestCategoryId", srSubCategory.getString("parentCode"));
					data.put("parentValue", srSubCategory.getString("parentValue"));
                    data.put("custRequestTypeId", srSubCategory.getString("grandparentCode"));
					data.put("grandparentValue", srSubCategory.getString("grandparentValue"));
					
					status = srSubCategory.getString("active");
					
					if(UtilValidate.isNotEmpty(status)){
						GenericValue getStatus = EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumCode", status,"enumTypeId",AlertCategoryConstant.STATUS_ID), null, false));
						if (UtilValidate.isNotEmpty(getStatus)){
							data.put("status",getStatus.getString("description"));
							
						}
					}
					
                    data.put("seqNo", srSubCategory.getString("sequenceNumber"));
                    data.put("createdOn", srSubCategory.getString("createdOn"));
                    data.put("createdBy", srSubCategory.getString("createdBy"));
                    data.put("modifiedOn", srSubCategory.getString("modifiedOn"));
                    data.put("modifiedBy", srSubCategory.getString("modifiedBy"));
                    
					results.add(data);
                }
            }
            
        }else {
	context.put("searchCriteria", searchCriteria);
}