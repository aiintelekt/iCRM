import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;

searchCriteria = request.getParameter("searchCriteria");
println('==searchCriteria==='+searchCriteria);
if(UtilValidate.isEmpty(searchCriteria)) {
               
} else {
	context.put("searchCriteria", searchCriteria);
}

auditLogList = EntityQuery.use(delegator).select("changedEntityName").from("EntityAuditLog").distinct().queryList();
changedEntityNames = org.fio.admin.portal.util.DataHelper.getDropDownOptions(auditLogList, "changedEntityName","changedEntityName");
context.put("changedEntityNames", changedEntityNames);				

auditLogList = EntityQuery.use(delegator).select("changedFieldName").from("EntityAuditLog").distinct().queryList();
changedFieldNames = org.fio.admin.portal.util.DataHelper.getDropDownOptions(auditLogList, "changedFieldName","changedFieldName");
context.put("changedFieldNames", changedFieldNames);	


