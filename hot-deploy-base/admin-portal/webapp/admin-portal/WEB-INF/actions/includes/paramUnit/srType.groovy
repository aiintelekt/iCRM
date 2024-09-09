import java.util.function.Function
import java.util.stream.Collectors

import org.fio.admin.portal.constant.AdminPortalConstant.AlertCategoryConstant
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
            fieldsToSelect.add("value");
            fieldsToSelect.add("code");
            fieldsToSelect.add("parentValue");
            fieldsToSelect.add("parentCode");
            fieldsToSelect.add("active");
            fieldsToSelect.add("sequenceNumber");
            fieldsToSelect.add("createdOn");
            fieldsToSelect.add("createdBy");
            fieldsToSelect.add("modifiedOn");
            fieldsToSelect.add("modifiedBy");
            String status = "";
            
            List < GenericValue > srTypes = EntityQuery.use(delegator).select(fieldsToSelect).from("CustRequestAssoc").orderBy("-lastUpdatedTxStamp").queryList();
            if (srTypes != null && srTypes.size() > 0) {
                for (GenericValue srType: srTypes) {
                    Map < String, Object > data = new HashMap < String, Object > ();
                    data.put("custRequestTypeId", srType.getString("code"));
                    data.put("description", srType.getString("value"));
                   
                    data.put("seqNo", srType.getString("sequenceNumber"));
                    status = srType.getString("active");
                    
                    if(UtilValidate.isNotEmpty(status)){
                        GenericValue getStatus = EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumCode", status,"enumTypeId",AlertCategoryConstant.STATUS_ID), null, false));
                        if (UtilValidate.isNotEmpty(getStatus)){
                            data.put("status",getStatus.getString("description"));
                        }
                    }
            		
            		data.put("createdOn", srType.getString("createdOn"));
                    data.put("createdBy", srType.getString("createdBy"));
                    data.put("modifiedOn", srType.getString("modifiedOn"));
                    data.put("modifiedBy",srType.getString("modifiedBy"));
                    results.add(data);
                            
                        }
                    }
                    
                }else {
	context.put("searchCriteria", searchCriteria);
}